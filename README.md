# Reitit - RestAPI - Swagger - Clojure

Refactored / Modularized Clojure REST API + Swagger documentation using Reitit

Original Source: [https://github.com/tzafrirben/rest-api-reitit](https://github.com/tzafrirben/rest-api-reitit)

It also uses middleware for case letter convention (`camelCase` from-to `kebab-case`), as in the original repo.

## Start

Clone this repo into your local hard drive and start the HTTP service:

```clj
> lein repl
(start)
```

REST API endpoints documentation using Swagger UI from [http://localhost:3000/api-docs](http://localhost:3000/api-docs).
