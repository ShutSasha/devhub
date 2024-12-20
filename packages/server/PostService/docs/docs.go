// Package docs Code generated by swaggo/swag. DO NOT EDIT
package docs

import "github.com/swaggo/swag"

const docTemplate = `{
    "schemes": {{ marshal .Schemes }},
    "swagger": "2.0",
    "info": {
        "description": "{{escape .Description}}",
        "title": "{{.Title}}",
        "contact": {},
        "version": "{{.Version}}"
    },
    "host": "{{.Host}}",
    "basePath": "{{.BasePath}}",
    "paths": {
        "/api/posts": {
            "get": {
                "description": "Retrieves a paginated list of posts with optional limit and page query parameters.",
                "consumes": [
                    "application/json"
                ],
                "produces": [
                    "application/json"
                ],
                "tags": [
                    "posts"
                ],
                "summary": "Get Paginated Posts",
                "parameters": [
                    {
                        "minimum": 1,
                        "type": "integer",
                        "description": "Number of posts per page (default is 10)",
                        "name": "limit",
                        "in": "query"
                    },
                    {
                        "minimum": 1,
                        "type": "integer",
                        "description": "Page number for pagination (default is 1)",
                        "name": "page",
                        "in": "query"
                    }
                ],
                "responses": {
                    "200": {
                        "description": "List of paginated posts",
                        "schema": {
                            "type": "array",
                            "items": {
                                "$ref": "#/definitions/storage.PostModel"
                            }
                        }
                    },
                    "500": {
                        "description": "Internal Server Error",
                        "schema": {}
                    }
                }
            },
            "post": {
                "description": "This endpoint allows a user to save a new post with a title, content, optional header image, and tags.",
                "consumes": [
                    "multipart/form-data"
                ],
                "produces": [
                    "application/json"
                ],
                "tags": [
                    "posts"
                ],
                "summary": "Save a new post",
                "parameters": [
                    {
                        "type": "string",
                        "description": "User ID of the user creating the post",
                        "name": "userId",
                        "in": "formData",
                        "required": true
                    },
                    {
                        "type": "string",
                        "description": "Title of the post",
                        "name": "title",
                        "in": "formData",
                        "required": true
                    },
                    {
                        "type": "string",
                        "description": "Content of the post",
                        "name": "content",
                        "in": "formData",
                        "required": true
                    },
                    {
                        "type": "file",
                        "description": "Header image for the post",
                        "name": "headerImage",
                        "in": "formData"
                    },
                    {
                        "type": "string",
                        "description": "Optional tags associated with the post (e.g., [tag1,tag2])",
                        "name": "tags",
                        "in": "formData"
                    }
                ],
                "responses": {
                    "200": {
                        "description": "Returns the details of the newly created post, including post ID, title, content, header image key, and tags",
                        "schema": {
                            "type": "object",
                            "additionalProperties": true
                        }
                    },
                    "400": {
                        "description": "Validation errors, request decoding failures, or file upload errors",
                        "schema": {
                            "type": "object",
                            "additionalProperties": true
                        }
                    },
                    "500": {
                        "description": "Internal server error",
                        "schema": {
                            "type": "object",
                            "additionalProperties": true
                        }
                    }
                }
            }
        },
        "/api/posts/get-popular-tags": {
            "get": {
                "description": "This endpoint retrieves a list of the most popular tags used in posts.",
                "consumes": [
                    "application/json"
                ],
                "produces": [
                    "application/json"
                ],
                "tags": [
                    "tags"
                ],
                "summary": "Get popular tags",
                "parameters": [
                    {
                        "type": "integer",
                        "default": 10,
                        "description": "Limit the number of tags returned",
                        "name": "limit",
                        "in": "query"
                    }
                ],
                "responses": {
                    "200": {
                        "description": "List of popular tags",
                        "schema": {
                            "type": "array",
                            "items": {
                                "type": "string"
                            }
                        }
                    },
                    "500": {
                        "description": "Internal server error",
                        "schema": {
                            "type": "object",
                            "additionalProperties": true
                        }
                    }
                }
            }
        },
        "/api/posts/search": {
            "get": {
                "description": "\"asc\" - from oldest to newest, \"desc\" - from newest to oldest. Default is \"desc\".",
                "consumes": [
                    "application/json"
                ],
                "produces": [
                    "application/json"
                ],
                "tags": [
                    "posts"
                ],
                "summary": "Search for posts",
                "parameters": [
                    {
                        "type": "string",
                        "example": "\"Golang\"",
                        "description": "Search query for post title or content",
                        "name": "q",
                        "in": "query"
                    },
                    {
                        "type": "array",
                        "items": {
                            "type": "string"
                        },
                        "collectionFormat": "multi",
                        "example": "\"dev\",\"tech\"",
                        "description": "Tags to filter posts",
                        "name": "tags[]",
                        "in": "query"
                    },
                    {
                        "enum": [
                            "asc",
                            "desc"
                        ],
                        "type": "string",
                        "default": "desc",
                        "description": "Sort order for posts by date",
                        "name": "sort",
                        "in": "query"
                    },
                    {
                        "type": "integer",
                        "default": 1,
                        "description": "Page number for pagination",
                        "name": "page",
                        "in": "query"
                    },
                    {
                        "type": "integer",
                        "default": 10,
                        "description": "Number of posts per page",
                        "name": "limit",
                        "in": "query"
                    }
                ],
                "responses": {
                    "200": {
                        "description": "List of posts",
                        "schema": {
                            "type": "array",
                            "items": {
                                "$ref": "#/definitions/storage.PostModel"
                            }
                        }
                    },
                    "400": {
                        "description": "Invalid pagination or query parameters",
                        "schema": {
                            "type": "object",
                            "additionalProperties": {
                                "type": "array",
                                "items": {
                                    "type": "string"
                                }
                            }
                        }
                    },
                    "404": {
                        "description": "No posts found",
                        "schema": {
                            "type": "object",
                            "additionalProperties": true
                        }
                    },
                    "500": {
                        "description": "Internal server error",
                        "schema": {
                            "type": "object",
                            "additionalProperties": {
                                "type": "array",
                                "items": {
                                    "type": "string"
                                }
                            }
                        }
                    }
                }
            }
        },
        "/api/posts/{id}": {
            "get": {
                "description": "This endpoint retrieves a post by its unique ID.",
                "consumes": [
                    "application/json"
                ],
                "produces": [
                    "application/json"
                ],
                "tags": [
                    "posts"
                ],
                "summary": "Get post by ID",
                "parameters": [
                    {
                        "type": "string",
                        "description": "Post ID",
                        "name": "id",
                        "in": "path",
                        "required": true
                    }
                ],
                "responses": {
                    "200": {
                        "description": "The requested post",
                        "schema": {
                            "$ref": "#/definitions/models.Post"
                        }
                    },
                    "400": {
                        "description": "Invalid postId format",
                        "schema": {
                            "type": "object",
                            "additionalProperties": true
                        }
                    },
                    "404": {
                        "description": "Post not found",
                        "schema": {
                            "type": "object",
                            "additionalProperties": true
                        }
                    },
                    "500": {
                        "description": "Internal server error",
                        "schema": {
                            "type": "object",
                            "additionalProperties": true
                        }
                    }
                }
            },
            "delete": {
                "description": "This endpoint allows a user to delete a post by its unique ID.",
                "consumes": [
                    "application/json"
                ],
                "produces": [
                    "application/json"
                ],
                "tags": [
                    "posts"
                ],
                "summary": "Delete a post by ID",
                "parameters": [
                    {
                        "type": "string",
                        "description": "Post ID",
                        "name": "id",
                        "in": "path",
                        "required": true
                    }
                ],
                "responses": {
                    "200": {
                        "description": "Success message",
                        "schema": {
                            "type": "object",
                            "additionalProperties": true
                        }
                    },
                    "400": {
                        "description": "Invalid postId format",
                        "schema": {
                            "type": "object",
                            "additionalProperties": true
                        }
                    },
                    "500": {
                        "description": "Internal server error",
                        "schema": {
                            "type": "object",
                            "additionalProperties": true
                        }
                    }
                }
            },
            "patch": {
                "description": "This endpoint allows a user to update an existing post with a new title, content, header image, and tags.",
                "consumes": [
                    "multipart/form-data"
                ],
                "produces": [
                    "application/json"
                ],
                "tags": [
                    "posts"
                ],
                "summary": "Update an existing post",
                "parameters": [
                    {
                        "type": "string",
                        "description": "Post ID",
                        "name": "id",
                        "in": "path",
                        "required": true
                    },
                    {
                        "type": "string",
                        "description": "Title of the post",
                        "name": "title",
                        "in": "formData"
                    },
                    {
                        "type": "string",
                        "description": "Content of the post",
                        "name": "content",
                        "in": "formData"
                    },
                    {
                        "type": "file",
                        "description": "Header image for the post",
                        "name": "headerImage",
                        "in": "formData"
                    },
                    {
                        "type": "string",
                        "description": "Optional tags associated with the post (e.g., [tag1,tag2])",
                        "name": "tags",
                        "in": "formData"
                    }
                ],
                "responses": {
                    "200": {
                        "description": "Model of the updated post",
                        "schema": {
                            "type": "object",
                            "additionalProperties": true
                        }
                    },
                    "400": {
                        "description": "Validation errors or request decoding failures",
                        "schema": {
                            "type": "object",
                            "additionalProperties": true
                        }
                    }
                }
            }
        },
        "/api/posts/{id}/dislike": {
            "post": {
                "description": "Adds a dislike to the specified post on behalf of a user.",
                "consumes": [
                    "application/json"
                ],
                "produces": [
                    "application/json"
                ],
                "tags": [
                    "Reactions"
                ],
                "summary": "Add a dislike to a post",
                "parameters": [
                    {
                        "type": "string",
                        "description": "Post ID",
                        "name": "id",
                        "in": "path",
                        "required": true
                    },
                    {
                        "description": "Request data",
                        "name": "body",
                        "in": "body",
                        "required": true,
                        "schema": {
                            "$ref": "#/definitions/react.ReactionRequest"
                        }
                    }
                ],
                "responses": {
                    "200": {
                        "description": "OK",
                        "schema": {
                            "type": "object",
                            "additionalProperties": true
                        }
                    },
                    "400": {
                        "description": "Bad Request",
                        "schema": {
                            "type": "object",
                            "additionalProperties": true
                        }
                    },
                    "500": {
                        "description": "Internal Server Error",
                        "schema": {
                            "type": "object",
                            "additionalProperties": true
                        }
                    }
                }
            }
        },
        "/api/posts/{id}/like": {
            "post": {
                "description": "Adds a like to the specified post on behalf of a user.",
                "consumes": [
                    "application/json"
                ],
                "produces": [
                    "application/json"
                ],
                "tags": [
                    "Reactions"
                ],
                "summary": "Add a like to a post",
                "parameters": [
                    {
                        "type": "string",
                        "description": "Post ID",
                        "name": "id",
                        "in": "path",
                        "required": true
                    },
                    {
                        "description": "Request data",
                        "name": "body",
                        "in": "body",
                        "required": true,
                        "schema": {
                            "$ref": "#/definitions/react.ReactionRequest"
                        }
                    }
                ],
                "responses": {
                    "200": {
                        "description": "OK",
                        "schema": {
                            "type": "object",
                            "additionalProperties": true
                        }
                    },
                    "400": {
                        "description": "Bad Request",
                        "schema": {
                            "type": "object",
                            "additionalProperties": true
                        }
                    },
                    "500": {
                        "description": "Internal Server Error",
                        "schema": {
                            "type": "object",
                            "additionalProperties": true
                        }
                    }
                }
            }
        }
    },
    "definitions": {
        "models.Post": {
            "type": "object",
            "properties": {
                "comments": {
                    "type": "array",
                    "items": {
                        "type": "string"
                    }
                },
                "content": {
                    "type": "string"
                },
                "createdAt": {
                    "type": "string"
                },
                "dislikes": {
                    "type": "integer"
                },
                "headerImage": {
                    "type": "string"
                },
                "likes": {
                    "type": "integer"
                },
                "saved": {
                    "type": "integer"
                },
                "tags": {
                    "type": "array",
                    "items": {
                        "type": "string"
                    }
                },
                "title": {
                    "type": "string"
                },
                "user": {
                    "type": "string"
                }
            }
        },
        "react.ReactionRequest": {
            "type": "object",
            "required": [
                "userId"
            ],
            "properties": {
                "userId": {
                    "type": "string"
                }
            }
        },
        "storage.CommentModel": {
            "type": "object",
            "properties": {
                "_id": {
                    "type": "string"
                },
                "commentText": {
                    "type": "string"
                },
                "createdAt": {
                    "type": "string"
                },
                "post": {
                    "type": "string"
                },
                "user": {
                    "$ref": "#/definitions/storage.UserModel"
                }
            }
        },
        "storage.PostModel": {
            "type": "object",
            "properties": {
                "_id": {
                    "type": "string"
                },
                "comments": {
                    "type": "array",
                    "items": {
                        "$ref": "#/definitions/storage.CommentModel"
                    }
                },
                "content": {
                    "type": "string"
                },
                "createdAt": {
                    "type": "string"
                },
                "dislikes": {
                    "type": "integer"
                },
                "headerImage": {
                    "type": "string"
                },
                "likes": {
                    "type": "integer"
                },
                "saved": {
                    "type": "integer"
                },
                "tags": {
                    "type": "array",
                    "items": {
                        "type": "string"
                    }
                },
                "title": {
                    "type": "string"
                },
                "user": {
                    "$ref": "#/definitions/storage.UserModel"
                }
            }
        },
        "storage.UserModel": {
            "type": "object",
            "properties": {
                "_id": {
                    "type": "string"
                },
                "avatar": {
                    "type": "string"
                },
                "devPoints": {
                    "type": "integer"
                },
                "name": {
                    "type": "string"
                },
                "username": {
                    "type": "string"
                }
            }
        }
    }
}`

// SwaggerInfo holds exported Swagger Info so clients can modify it
var SwaggerInfo = &swag.Spec{
	Version:          "",
	Host:             "",
	BasePath:         "",
	Schemes:          []string{},
	Title:            "",
	Description:      "",
	InfoInstanceName: "swagger",
	SwaggerTemplate:  docTemplate,
}

func init() {
	swag.Register(SwaggerInfo.InstanceName(), SwaggerInfo)
}
