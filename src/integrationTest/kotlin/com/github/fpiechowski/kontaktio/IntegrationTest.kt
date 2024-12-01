package com.github.fpiechowski.kontaktio

import com.github.tomakehurst.wiremock.client.WireMock
import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.longs.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.ktor.client.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlin.system.measureTimeMillis

class IntegrationTest : FreeSpec({

    beforeSpec { WireMock.reset() }

    "health check" - {
        val response = httpClient.get("$appUrl/health-check")

        "responds 200 OK" {
            response.shouldHaveStatus(200)
            response.bodyAsText() shouldBe "OK"
        }
    }

    WireMock.configureFor(apiHost, apiPort)

    "building" - {
        "get" - {
            val id = 1

            "successful response" - {
                WireMock.stubFor(
                    WireMock.get(WireMock.urlPathEqualTo("/v2/locations/buildings/$id"))
                        .withHeader(ApiKeyHttpHeader, WireMock.equalTo("fake"))
                        .willReturn(
                            WireMock.aResponse()
                                .withBody(testGetBuildingApiResponse)
                                .withHeader("Content-Type", "application/json")
                                .withStatus(200)
                        )
                )

                val response = httpClient.get("$appUrl/buildings/$id") {
                    header(HttpHeaders.XRequestId, "test")
                }

                "responds 200 OK and building JSON" {
                    response.shouldHaveStatus(200)
                    response.bodyAsText() shouldEqualJson expectedGetBuildingAppResponse

                    WireMock.verify(WireMock.getRequestedFor(WireMock.urlPathEqualTo("/v2/locations/buildings/$id")))
                }
            }

            "unsuccessful response" - {
                WireMock.stubFor(
                    WireMock.get(WireMock.urlPathEqualTo("/v2/locations/buildings/$id"))
                        .withHeader(ApiKeyHttpHeader, WireMock.equalTo("fake"))
                        .willReturn(
                            WireMock.aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withStatus(404)
                        )
                )

                val response = httpClient.get("$appUrl/buildings/$id") {
                    header(HttpHeaders.XRequestId, "test")
                }

                "responds 404 Not Found" {
                    response.shouldHaveStatus(404)

                    WireMock.verify(WireMock.getRequestedFor(WireMock.urlPathEqualTo("/v2/locations/buildings/$id")))
                }
            }

            "unparseable response" - {
                WireMock.stubFor(
                    WireMock.get(WireMock.urlPathEqualTo("/v2/locations/buildings/$id"))
                        .withHeader(ApiKeyHttpHeader, WireMock.equalTo("fake"))
                        .willReturn(
                            WireMock.aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withStatus(200)
                                .withBody("can't parse this")
                        )
                )

                val response = httpClient.get("$appUrl/buildings/$id") {
                    header(HttpHeaders.XRequestId, "test")
                }

                "responds 500 Internal Server Error" {
                    response.shouldHaveStatus(500)
                    response.bodyAsText() shouldEqualJson expectedUnparseableErrorResponse

                    WireMock.verify(WireMock.getRequestedFor(WireMock.urlPathEqualTo("/v2/locations/buildings/$id")))
                }
            }
        }
    }

    "performance".config(enabled = false) - {
        WireMock.stubFor(
            WireMock.get(WireMock.urlPathEqualTo("/v2/locations/buildings/1"))
                .withHeader(ApiKeyHttpHeader, WireMock.equalTo("fake"))
                .willReturn(
                    WireMock.aResponse()
                        .withBody(testGetBuildingApiResponse)
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                )
        )

        val sample = 1000
        val duration = measureTimeMillis {
            (1..sample).map { id ->
                async {
                    httpClient.get("$appUrl/buildings/1") {
                        header(HttpHeaders.XRequestId, "test")
                    }
                }
            }.awaitAll()
        }

        withClue("App is not performing well") {
            ((duration / sample) * 50).also {
                logger.info { "$it ms per 50 requests" }
            }.shouldBeLessThan(1000)
        }
    }
}) {
    companion object {
        val logger = KotlinLogging.logger("IntegrationTest")
        val httpClient = HttpClient {
            install(Logging) {
                level = LogLevel.BODY
            }
        }

        val appUrl = System.getenv("APP_URL") ?: "http://localhost:8080"
        val apiHost = System.getenv("API_HOST") ?: "localhost"
        val apiPort = System.getenv("API_PORT")?.toInt() ?: 8081
    }
}

const val testGetBuildingApiResponse = """
    {
      "content": [
        {
          "id": 0,
          "name": "string",
          "address": "string",
          "description": "string",
          "floors": [
            {
              "id": 0,
              "name": "string",
              "height": 0,
              "width": 0,
              "anchorLat": 0,
              "anchorLng": 0,
              "rotation": 0,
              "properties": {
                "EgQhanFqevOm": "string",
                "OfrViMjzwNPS": "string",
                "taVMBxTzdnKH": "string"
              },
              "level": 0,
              "latLngGeojson": {
                "type": "string",
                "geometry": {
                  "type": "string",
                  "coordinates": [
                    {
                      "0": [
                        {
                          "0": [
                            {}
                          ],
                          "1": [
                            {}
                          ],
                          "2": [
                            {}
                          ],
                          "3": [
                            {}
                          ],
                          "4": [
                            {}
                          ]
                        }
                      ]
                    }
                  ]
                }
              },
              "xyGeojson": {
                "type": "string",
                "geometry": {
                  "type": "string",
                  "coordinates": [
                    {
                      "0": [
                        {
                          "0": [
                            {}
                          ],
                          "1": [
                            {}
                          ],
                          "2": [
                            {}
                          ],
                          "3": [
                            {}
                          ],
                          "4": [
                            {}
                          ]
                        }
                      ]
                    }
                  ]
                }
              },
              "imageLatLngGeojson": {
                "type": "string",
                "geometry": {
                  "type": "string",
                  "coordinates": [
                    {
                      "0": [
                        {
                          "0": [
                            {}
                          ],
                          "1": [
                            {}
                          ],
                          "2": [
                            {}
                          ],
                          "3": [
                            {}
                          ],
                          "4": [
                            {}
                          ]
                        }
                      ]
                    }
                  ]
                }
              },
              "imageXyGeojson": {
                "type": "string",
                "geometry": {
                  "type": "string",
                  "coordinates": [
                    {
                      "0": [
                        {
                          "0": [
                            {}
                          ],
                          "1": [
                            {}
                          ],
                          "2": [
                            {}
                          ],
                          "3": [
                            {}
                          ],
                          "4": [
                            {}
                          ]
                        }
                      ]
                    }
                  ]
                }
              }
            }
          ],
          "campus": {
            "id": 0,
            "name": "string"
          },
          "lat": 0,
          "lng": 0,
          "latLngGeojson": {
            "type": "string",
            "geometry": {
              "type": "string",
              "coordinates": [
                {
                  "0": [
                    {
                      "0": [
                        {}
                      ],
                      "1": [
                        {}
                      ],
                      "2": [
                        {}
                      ],
                      "3": [
                        {}
                      ],
                      "4": [
                        {}
                      ]
                    }
                  ]
                }
              ]
            }
          },
          "xyGeojson": {
            "type": "string",
            "geometry": {
              "type": "string",
              "coordinates": [
                {
                  "0": [
                    {
                      "0": [
                        {}
                      ],
                      "1": [
                        {}
                      ],
                      "2": [
                        {}
                      ],
                      "3": [
                        {}
                      ],
                      "4": [
                        {}
                      ]
                    }
                  ]
                }
              ]
            }
          }
        }
      ]
    }
"""

const val expectedGetBuildingAppResponse = """
    {
      "id": 0,
      "name": "string",
      "address": "string",
      "floors": [
        {
          "level": 0,
          "image": {
            "type": "string",
            "geometry": {
              "type": "string",
              "coordinates": [
                {
                  "0": [
                    {
                      "0": [
                        {}
                      ],
                      "1": [
                        {}
                      ],
                      "2": [
                        {}
                      ],
                      "3": [
                        {}
                      ],
                      "4": [
                        {}
                      ]
                    }
                  ]
                }
              ]
            }
          },
          "properties": {
            "EgQhanFqevOm": "string",
            "OfrViMjzwNPS": "string",
            "taVMBxTzdnKH": "string"
          }
        }
      ]
    }
"""

const val expectedUnparseableErrorResponse = """
    {
        "message": "Failed to GET building with ID = 1",
        "throwable": "io.ktor.serialization.JsonConvertException: Illegal input: Unexpected JSON token at offset 0: Expected start of the object '{', but had 'c' instead at path: ${'$'}
JSON input: can't parse this"
    }
"""
