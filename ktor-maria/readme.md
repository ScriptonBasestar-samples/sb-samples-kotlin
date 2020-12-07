Board
=====

## 개요

간단한 게시판 기능

### 기능목표

* 경량 마이크로 서비스
* API GW 내에서 동작하는 것을 가정
* 유동적으로 scale out 가능한 설계

### 사용기술

* kotlin
* ktor
* exposed
* gradle
* mariadb
* docker
* junit


## 테스트

* 메모리DB 사용 기능단위 테스트
* docker-mariadb 사용하는 CRUD 테스트


## 실행

`docker-compose up`
