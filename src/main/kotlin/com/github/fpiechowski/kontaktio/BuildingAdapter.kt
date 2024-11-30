package com.github.fpiechowski.kontaktio

import arrow.core.raise.catch
import arrow.core.raise.either
import arrow.core.raise.recover
import com.github.fpiechowski.kontaktio.Building
import com.github.fpiechowski.kontaktio.BuildingResponse.Factory
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import com.github.fpiechowski.kontaktio.Building as DomainBuilding
import com.github.fpiechowski.kontaktio.Building.Floor as DomainFloor

private val logger = KotlinLogging.logger("BuildingAdapter")

private fun getBuildingPath(id: Id) = "/v2/locations/buildings/$id"

val ApiKeyHttpHeader get() = "Api-Key"

fun getBuilding(httpClient: HttpClient, config: Config): GetBuilding = { id ->
    either {
        catch(
            block = {
                val url = config.kontaktApi.baseUrl.removeSuffix("/") + getBuildingPath(id)
                val response = httpClient.get(url) {
                    header(ApiKeyHttpHeader, config.kontaktApi.apiKey)
                }

                when {
                    response.status.isSuccess() -> response.body<SourceBuildingResponse>()
                        .toDomain()

                    else -> {
                        val bodyText = response.bodyAsText()
                        raise(
                            UnsuccessfulResponseError(
                                response.status,
                                bodyText,
                                "Unsuccessful response: ${response.status}, $bodyText"
                            )
                        )
                    }
                }
            }, catch = {
                raise(BuildingError("Failed to GET building with ID = $id", throwable = it))
            }
        )
    }
}

data class UnsuccessfulResponseError(
    val status: HttpStatusCode,
    val bodyText: String,
    override val message: String,
    override val throwable: Throwable? = null,
    override val cause: KontaktError? = null
) : BuildingError(message, cause, throwable)

@Resource("/buildings/{id}")
class BuildingById(
    val id: Long
)

fun Application.building(getBuilding: GetBuilding, responseFactory: BuildingResponse.Factory) {
    routing {
        get<BuildingById> { params ->
            recover({
                val building = getBuilding(params.id).bind()
                call.respond(responseFactory.from(building))
            }) {
                logger.error(it.throwable) {
                    "Error on GET building by id = ${params.id} ${it::class.simpleName}: ${it.message}"
                }
                when (it) {
                    is UnsuccessfulResponseError -> call.respond(it.status, it.bodyText)
                    else -> call.respond(HttpStatusCode.InternalServerError, ErrorResponse.from(it))
                }
            }
        }
    }
}

@Serializable
data class SourceBuildingResponse(
    val content: List<Building>
) {
    @Serializable
    data class Building(
        val id: Id,
        val name: String,
        val address: String,
        val floors: List<Floor>
    ) {
        @Serializable
        data class Floor(
            val level: Int,
            val imageLatLngGeojson: JsonElement,
            val properties: Map<String, String>
        ) {
            fun toDomain(): DomainFloor = DomainFloor(
                level = level,
                image = imageLatLngGeojson.toString(),
                properties = properties
            )
        }
    }

    fun toDomain() = with(content.first()) {
        DomainBuilding(
            id = id,
            name = name,
            address = address,
            floors = floors.map { it.toDomain() }
        )
    }
}

@Serializable
data class BuildingResponse(
    val id: Id,
    val name: String,
    val address: String,
    val floors: List<Floor>
) {
    @Serializable
    data class Floor(
        val level: Int,
        val image: JsonElement,
        val properties: Map<String, String>
    ) {
        companion object {
            fun from(floor: Building.Floor, json: Json) = Floor(
                floor.level,
                json.parseToJsonElement(floor.image),
                floor.properties
            )
        }
    }

    fun interface Factory {
        fun from(building: DomainBuilding): BuildingResponse

        companion object {
            fun withDecodedImage(json: Json) =
                Factory { building ->
                    BuildingResponse(
                        building.id,
                        building.name,
                        building.address,
                        building.floors.map { Floor.from(it, json) },
                    )
                }
        }
    }
}
