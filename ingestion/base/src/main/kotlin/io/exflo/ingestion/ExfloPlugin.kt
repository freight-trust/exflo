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

package io.exflo.ingestion

import io.exflo.ingestion.KoinModules.eventsModule
import io.exflo.ingestion.KoinModules.stateModule
import io.exflo.ingestion.KoinModules.storageModule
import io.exflo.ingestion.tokens.precompiled.PrecompiledContractsFactory
import io.exflo.ingestion.tracker.BlockWriter
import io.exflo.ingestion.tracker.ChainTracker
import org.apache.logging.log4j.LogManager
import org.hyperledger.besu.cli.config.EthNetworkConfig
import org.hyperledger.besu.cli.config.NetworkName
import org.hyperledger.besu.config.GenesisConfigFile
import org.hyperledger.besu.ethereum.chain.GenesisState
import org.hyperledger.besu.ethereum.core.PrivacyParameters
import org.hyperledger.besu.ethereum.mainnet.MainnetProtocolSchedule
import org.hyperledger.besu.ethereum.mainnet.ProtocolSchedule
import org.hyperledger.besu.ethereum.mainnet.ScheduleBasedBlockHeaderFunctions
import org.hyperledger.besu.plugin.BesuContext
import org.hyperledger.besu.plugin.BesuPlugin
import org.hyperledger.besu.plugin.services.PicoCLIOptions
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.dsl.module

@Suppress("MemberVisibilityCanBePrivate")
abstract class ExfloPlugin<T : ExfloCliOptions> : BesuPlugin {

    private val log = LogManager.getLogger()

    protected abstract val name: String

    protected abstract val options: T

    protected lateinit var context: BesuContext

    private lateinit var blockWriter: BlockWriter

    private val rocksDBPlugin = RocksDBPlugin()

    override fun register(context: BesuContext) {
        rocksDBPlugin.register(context)

        log.debug("Registering plugin")
        this.context = context

        val cmdlineOptions = context.getService(PicoCLIOptions::class.java)
        check(!cmdlineOptions.isEmpty) { "Expecting a PicoCLIO options to register CLI options with, but none found." }

        val cliOptions = cmdlineOptions.get()
        cliOptions.addPicoCLIOptions(name, options)

        log.info("Plugin registered")
    }

    protected abstract fun implKoinModules(): List<Module>

    protected open fun implStart(koinApp: KoinApplication) {}

    override fun start() {
        log.debug("Starting plugin")

        try {
            rocksDBPlugin.start()

            // TODO: Review with Besu devs if there's a better way of obtaining this info without resorting on env variables
            val networkName = NetworkName.valueOf(System.getenv("BESU_NETWORK").toUpperCase())
            val networkConfig = EthNetworkConfig.getNetworkConfig(networkName)

            log.debug("Network name: $networkName | Network Config: $networkConfig")

            val genesisConfigFile = GenesisConfigFile.fromConfig(networkConfig.genesisConfig)

            // TODO needs to change based on network properties, should be good for mainnet and ropsten
            val protocolSchedule = MainnetProtocolSchedule.fromConfig(
                genesisConfigFile.configOptions,
                PrivacyParameters.DEFAULT,
                true
            )

            // Register custom precompiled contracts
            PrecompiledContractsFactory.register(protocolSchedule, networkConfig.networkId)

            val genesisState = GenesisState.fromConfig(genesisConfigFile, protocolSchedule)

            // create a module for injecting various basic context objects
            val contextModule = module {
                single { context }
                single { networkName }
                single { networkConfig }
                single { protocolSchedule }
                single { genesisState }
                single { ScheduleBasedBlockHeaderFunctions.create(get<ProtocolSchedule<Void>>()) }
                single { ChainTracker(get(), get(), get(), get(), get()) }
            }

            // implementation specific DI modules which we combine with other standard modules
            val implKoinModules = implKoinModules()

            // start the DI system
            val koinApp = startKoin {
                if (log.isDebugEnabled) printLogger()
                modules(
                    listOf(
                        contextModule,
                        eventsModule,
                        storageModule,
                        stateModule
                    ) + implKoinModules
                )
            }

            // allow derived plugins to execute some start logic before we start publishing
            implStart(koinApp)

            blockWriter = koinApp.koin.get()
            blockWriter.start()
        } catch (ex: Exception) {
            log.error("Failed to start", ex)
        }
    }

    override fun stop() {
        log.debug("Stopping plugin")
        blockWriter.stop()
        stopKoin()
        rocksDBPlugin.stop()
    }
}

/**
 * Defines common cli options shared among implementors.
 */
interface ExfloCliOptions {

    var startBlockOverride: Long?

    var maxForkSize: Int

    enum class ProcessableEntity {
        HEADER,
        BODY,
        RECEIPTS,
        TRACES
    }
}

object ExfloCliDefaultOptions {
    const val EXFLO_POSTGRES_PLUGIN_ID: String = "exflo-postgres"
    const val EXFLO_KAFKA_PLUGIN_ID: String = "exflo-kafka"

    const val MAX_FORK_SIZE: Int = 192
}
