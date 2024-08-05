# 여행 타입 테스트 (Trip Type Test) Backend
 
 이 프로젝트는 여행 타입 테스트> 서비스의 백엔드입니다.
 
 여행 타입 테스트는 일행과 함께 여행을 떠나기 전 서로 다른 스타일을 알아보고 의견을 조율하는데 도움을 주는 웹 어플리케이션입니다.

## Demo

[https://trip-chemistry.vercel.app](https://trip-chemistry.vercel.app) (version 1.0)


## Stack
- Spring 
- Project Reactor
  
  Reactive Stream 시스템으로 구현되었습니다.


## More

프로젝트의 프론트엔드는 [이곳](https://github.com/EAexist/trip-chemistry)에서 확인할 수 있습니다.

프로젝트의 기획, 개발 및 최적화 과정에 대한 기록은 [이곳](https://bush-hippodraco-59e.notion.site/3b8d391b051447d5a2fc444a373d6e99)에서 확인할 수 있습니다.


## Development

To run the server, you need own MongoDB Server. You should provide valid URI and database name in src/main/resources/appliction-dev.yml as following. Otherwise, the server fails to run.

```sh
spring:
    data:
        mongodb: 
            uri: # Your MongoDB URI
            database: # Your MongoDB Database Name
```



Once you provided MongoDB URI and database name, build and run the server in development mode:

```sh
gradle bootRun
```
Open [http://localhost:8080](http://localhost:8080) to access the server in the browser.



To use Kakao Login API, you should register and get your own API key in [Kakao Developers](https://developers.kakao.com/product/kakaoLogin). You should provide valid client id, client secret and redirect uri in src/main/resources/appliction-dev.yml as following. You can still run the server without registering and using Kakao API.

```sh
spring:
    security: 
        oauth2:
            client:
                registration:
                    kakao:
                        client-id: # Your Kakao API client id
                        client-secret: # Your Kakao API client secret
                        client-authentication-method: client_secret_post
                        authorization-grant-type: authorization_code
                        client-name: kakao
                        scope:
                        - profile_nickname
                        redirect-uri: # Your Kakao API redirect-uri
```
