kote2e
======

E2E testing library for *kotlin*.
This library provides *internal DSL* for E2E testing.

## Features

 * Karate-like fuzzy matcher

## DSL Syntax

### Background clause

Set the global backend of testing.

#### url

You can set the base url to test

    val bg = Background {
        url = "http://example.com/"
    }


### Given clause

#### path

Path of URL.

    bg.Given {
        path = "documents/$documentId/download"
    }

#### request

You can use String or any object to send request body.
There're 3 methods to set request body.

`fun request(body: String)`:

    bg.Given {
        request = """{"hello":"world"}"""
    }

`fun request(body: ByteArray)`:

    bg.Given {
        request = read("foo.png")
    }

`fun request(body: Any)`:

    bg.Given {
        path = "/"
        request(mapOf("hello" to "world"))
    }

kote2e serialize object using Jackson.

    
#### method

The HTTP verb - get, post, put, delete, patch, options, head, connect, trace.

       bg.Given {
           method = "POST"
       }

#### param

TODO: support

#### header

Send HTTP header.

    bg.Given { header("Content-Type", "application/json") }

## See also

 * https://github.com/intuit/karate
 * http://rest-assured.io/
