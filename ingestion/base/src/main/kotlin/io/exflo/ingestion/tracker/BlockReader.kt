/*
 * Copyright (c) 2020 41North.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.exflo.ingestion.tracker

import io.exflo.domain.BlockTrace
import io.exflo.ingestion.core.FullBlock
import io.exflo.ingestion.extensions.toBalanceDeltas
import io.exflo.ingestion.extensions.touchedAccounts
import io.exflo.ingestion.tracer.BlockTracer
import org.hyperledger.besu.cli.config.EthNetworkConfig
import org.hyperledger.besu.ethereum.chain.BlockchainStorage
import org.hyperledger.besu.ethereum.core.Account
import org.hyperledger.besu.ethereum.core.Block
import org.hyperledger.besu.ethereum.core.BlockBody
import org.hyperledger.besu.ethereum.core.BlockHeader
import org.hyperledger.besu.ethereum.core.Hash
import org.hyperledger.besu.ethereum.core.TransactionReceipt
import org.hyperledger.besu.ethereum.worldstate.WorldStateArchive
import org.hyperledger.besu.util.uint.UInt256
import org.koin.core.KoinComponent
import org.koin.core.inject

class BlockReader : KoinComponent {

    private val blockchainStorage: BlockchainStorage by inject()
    private val blockTracer: BlockTracer by inject()
    private val networkConfig: EthNetworkConfig by inject()
    private val worldStateArchive: WorldStateArchive by inject()

    fun chainHead(): Hash? = blockchainStorage.chainHead.orElse(null)

    fun fullBlock(
        hash: Hash,
        withHeader: Boolean = true,
        withBody: Boolean = true,
        withReceipts: Boolean = true,
        withTrace: Boolean = true
    ): FullBlock? = block(hash)?.let { fullBlock(it, withHeader, withBody, withReceipts, withTrace) }

    fun fullBlock(
        block: Block,
        withHeader: Boolean = true,
        withBody: Boolean = true,
        withReceipts: Boolean = true,
        withTrace: Boolean = true
    ): FullBlock? =
        block.let {
            val header = if (withHeader) it.header else null
            val totalDifficulty =
                if (withHeader) requireNotNull(totalDifficulty(it.hash)) { "totalDifficulty not found" } else null

            val body = if (withBody) it.body else null
            val receipts =
                if (withBody && withReceipts) requireNotNull(receipts(it.hash)) { "receipts not found" } else emptyList()

            val trace = if (withTrace) requireNotNull(trace(it.hash)) { "trace not found" } else null
            val touchedAccounts = trace?.let { t -> touchedAccounts(t) }
            val balanceDeltas = trace?.toBalanceDeltas(
                it.hash,
                it.header.coinbase,
                it.body.ommers.map { h -> Pair(h.hash, h.coinbase) }.toMap()
            )

            FullBlock(
                header,
                body,
                receipts,
                totalDifficulty,
                trace,
                touchedAccounts,
                balanceDeltas
            )
        }

    fun headersFrom(head: Hash, count: Int): List<BlockHeader> {

        var hash = head
        var headers = listOf<BlockHeader>()

        do {
            val header = this.header(hash)
            header?.let { h ->
                headers = headers + h
                hash = h.parentHash
            }
        } while (header != null && header.number >= BlockHeader.GENESIS_BLOCK_NUMBER && headers.size < count)

        return headers.toList()
    }

    fun header(hash: Hash): BlockHeader? =
        blockchainStorage.getBlockHeader(hash).orElse(null)

    fun header(number: Long): BlockHeader? =
        blockchainStorage.getBlockHash(number)
            .flatMap { hash -> blockchainStorage.getBlockHeader(hash) }
            .orElse(null)

    fun body(hash: Hash): BlockBody? =
        blockchainStorage.getBlockBody(hash).orElse(null)

    fun block(hash: Hash): Block? =
        header(hash)?.let { header -> Block(header, requireNotNull(body(hash)) { "body not found" }) }

    fun receipts(hash: Hash): List<TransactionReceipt>? =
        blockchainStorage.getTransactionReceipts(hash).orElse(null)

    fun trace(hash: Hash): BlockTrace? =
        block(hash)
            ?.let { block -> Pair(block, requireNotNull(receipts(hash)) { "receipts not found" }) }
            ?.let { (block, receipts) -> blockTracer.trace(block, receipts) }

    fun totalDifficulty(hash: Hash): UInt256? =
        blockchainStorage.getTotalDifficulty(hash).orElse(null)

    fun touchedAccounts(trace: BlockTrace): List<Account> = trace.touchedAccounts(networkConfig, worldStateArchive)
}
