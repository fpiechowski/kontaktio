package com.github.fpiechowski.kontaktio

import com.github.tomakehurst.wiremock.client.WireMock
import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

class IntegrationTest : FreeSpec({

    val logger = KotlinLogging.logger("IntegrationTest")

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
                        .willReturn(
                            WireMock.aResponse()
                                .withBody(testGetBuildingApiResponse)
                                .withHeader("Content-Type", "application/json")
                                .withStatus(200)
                        )
                )

                val response = httpClient.get("$appUrl/buildings/$id")

                "responds 200 OK and building JSON" {
                    response.shouldHaveStatus(200)
                    response.bodyAsText() shouldEqualJson expectedGetBuildingAppResponse
                }
            }

            "unsuccessful response" - {
                WireMock.stubFor(
                    WireMock.get(WireMock.urlPathEqualTo("/v2/locations/buildings/$id"))
                        .willReturn(
                            WireMock.aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withStatus(404)
                        )
                )

                val response = httpClient.get("$appUrl/buildings/$id")

                "responds 404 Not Found" {
                    response.shouldHaveStatus(404)
                }
            }

            "unparseable response" - {
                WireMock.stubFor(
                    WireMock.get(WireMock.urlPathEqualTo("/v2/locations/buildings/$id"))
                        .willReturn(
                            WireMock.aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withStatus(200)
                                .withBody("can't parse this")
                        )
                )

                val response = httpClient.get("$appUrl/buildings/$id")

                "responds 500 Internal Server Error" {
                    response.shouldHaveStatus(500)
                    response.bodyAsText() shouldEqualJson expectedUnparseableErrorResponse
                }
            }
        }
    }
}) {
    companion object {
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
