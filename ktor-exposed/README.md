Board
=====

## Overview

Sample web application using
`Kotlin + Ktor + Exposed`

### Features

* Light Weight web API
* Application run inside G/W
* Easy for Scaleoutable
* Just for sample(DB, TEST)

### 사용기술

* kotlin
* ktor
* exposed
* gradle
* mariadb
* docker
* junit


## Test

### ManualTest

단순 기능 동작 테스트

#### CheckTest

* 외부 API

### Unit Test

개발, 빌드시 자동화 테스트

#### mock test

* dao(model) test - (dao - h2 memory db)
* service(handler) test - (service - dao - h2 memory db)
* controller(http, router) test - (http - controller - mock service)
* api test - (http - controller - service - model - )

#### integration test

* http test - docker mariadb

### IntegrationTest(CI/CD)

운영 배포시 테스트

#### FunctionTest

* following to test scenario

#### LoadTest

* staging cluster
* dev cluster
* jmeter

#### Health Check

* 컨테이너 단위
* 전체 응답
