Board
=====

## Overview

Sample web application using
`Kotlin + Ktor + Ktorm`

ktorm은 db manipulation은 지원하지 않고 query만 지원

exposed에서는 orm으로 db생성을 하는데 지원이 미약해서

디비가 아닌 exposed에서 지원되는 범위 내에서 사용하는 경우가 있는데 어설플 바에는 이게 오히려 나을지도 모르겠다

jvm계열이면 kotlin 지원이 조금 약하해도 그냥 jpa-hibernate 쓰는게 제일 나을 것 같고

ktorm 뭔가 좀 부실한데 DSL이랑 쿼리 문법도 간단하지가 않고

### Features

* Lightweight web API
* Application run inside G/W
* Easy for Scaleoutable
* Just for sample(DB, TEST)

### 사용기술

* kotlin
* ktor
* ktorm
* gradle
* mariadb
* docker
* junit
