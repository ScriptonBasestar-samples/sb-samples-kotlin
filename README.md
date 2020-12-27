sb-samples-kotlin
=================


## Test

### Source Level

단순 기능 동작 테스트

#### CheckTest

수동 테스트

* 외부 API

#### Unit Test

개발, 빌드시 자동화 테스트

* dao(model) test - (dao - h2 memory db)
* service(handler) test - (service - dao - h2 memory db)
* controller(http, router) test - (http - controller - mock service)
* api test - (http - controller - service - model - )

#### Bake test

* http test - docker mariadb

### Integration

운영 배포시 다른것과 연동 테스트

#### FunctionTest

* following to test scenario

#### LoadTest

* staging cluster
* dev cluster
* jmeter

### Health Check

* 컨테이너 단위
* 전체 응답
