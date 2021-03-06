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

package io.exflo.ingestion.core

import io.exflo.ingestion.extensions.hexToLong
import io.exflo.ingestion.extensions.toBalance
import java.util.NavigableMap
import org.hyperledger.besu.config.GenesisAllocation
import org.hyperledger.besu.ethereum.core.Account
import org.hyperledger.besu.ethereum.core.AccountStorageEntry
import org.hyperledger.besu.ethereum.core.Address
import org.hyperledger.besu.ethereum.core.Hash
import org.hyperledger.besu.ethereum.core.Wei
import org.hyperledger.besu.util.bytes.Bytes32
import org.hyperledger.besu.util.bytes.BytesValue
import org.hyperledger.besu.util.uint.UInt256

/**
 * Implementation of [Account] that holds its data as properties.
 *
 * It's used for generating synthetic [GenesisAllocation] accounts for genesis block.
 */
class InMemoryAccount(
    private val address: Address,
    private val balance: Wei,
    private val nonce: Long,
    private val code: BytesValue?,
    private val codeHash: Hash?
) : Account {

    override fun getBalance(): Wei = balance

    override fun getStorageValue(key: UInt256?): UInt256 {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getAddressHash(): Hash = Hash.fromHexString(address.hexString)

    override fun getOriginalStorageValue(key: UInt256?): UInt256 {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getAddress(): Address = address

    override fun getVersion(): Int {
        throw UnsupportedOperationException("not implemented")
    }

    override fun storageEntriesFrom(startKeyHash: Bytes32?, limit: Int): NavigableMap<Bytes32, AccountStorageEntry> {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getCode(): BytesValue = code ?: BytesValue.EMPTY

    override fun getNonce(): Long = nonce

    override fun getCodeHash(): Hash = codeHash ?: Hash.EMPTY

    companion object {

        fun fromGenesisAllocation(allocation: GenesisAllocation): InMemoryAccount {

            val nonce = allocation.nonce.hexToLong()
            val address = Address.fromHexString(allocation.address)
            val balance = allocation.balance.toBalance()
            val code = allocation.code?.let { BytesValue.fromHexString(it) }

            return InMemoryAccount(address, balance, nonce, code, null)
        }
    }
}
