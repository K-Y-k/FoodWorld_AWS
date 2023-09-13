# :rocket: FoodWorld_Project
기존 프로젝트를 AWS EC2 서버, RDS MariaDB로 마이그레이션 및 CI/CD, 무중단 배포 (2023.08.28~09.12)

- 개발 인원: 1명

- 개발 기간: 2023.01 ~ 2023.04
  
- 개발 기간의 github: https://github.com/K-Y-k/KYKProject_FoodWorld_Spring

- AWS로 마이그레이션 및 배포 기간: 2023.09

- URL: http://ec2-15-165-148-241.ap-northeast-2.compute.amazonaws.com


# :page_facing_up: 목차
- <a href="#0"> 어떤 프로젝트인가요?(프로젝트의 목적) </a> 
- <a href="#2"> 사용 기술 </a> 
   * <a href="#2.1"> FrontEnd </a> 
   * <a href="#2.2"> BackEnd </a>
   * <a href="#2.3"> DevOps </a>
   * <a href="#2.4"> 기타 주요 라이브러리 </a>
- <a href="#3"> E-R 다이어그램 </a>
- <a href="#4"> 아키텍처 구성 </a>
- <a href="#5"> 시연 영상 </a>
- <a href="#6"> 주요 기능 </a>
- <a href="#7"> 어려웠던 점 및 극복 </a> 
- <a href="#8"> 프로젝트를 통해 느낀점 </a> 
- <a href="#9"> 프로젝트 관련 추가 포스팅 </a> 


## <b id="0"> ❓ 어떤 프로젝트인가요?(프로젝트의 목적)</b>
백엔드 웹 개발자가 되기 위해 Jdbc, DB, Servlet의 기초를 다지고 

Spring과 관련된 Spring 기초, HTTP 네트워크, MVC 패턴, DB 연결, JPA, SpringDataJPA, queryDsl을 학습한 후 배운 내용을 나의 것으로 만들기 위해 처음 구현해본 프로젝트입니다.

사람에게 중요한 것중의 하나인 음식을 타깃으로 선정하고 공감대를 형성하기 좋은 음식의 주제로 소통을 할 수 있고 음식 메뉴를 선정할 때 도움을 제공하는 서비스로

기본적으로 게시글/댓글을 작성할 수 있고 추가로 다른 회원과 실시간 채팅을 나눌 수 있고 회원들이 등록한 메뉴를 랜덤으로 추첨할 수 있습니다.


## <b id="1"> 🏢 개발 환경 </b>
- ### IDE
  * IntelliJ IDEA Community Edition 2022
  * Visual Studio Code 1.7.8
  * Postman
  * GitHub

## <b id="2"> 🛠 사용 기술 </b>
### <b id="2.1"> FrontEnd </b>
- #### Language / FrameWork / Library
  * Html/Css
  * Javascript
  * Bootstrap 5.2
  * Jquery
  * Thymeleaf

### <b id="2.2"> BackEnd </b>
- #### Language / FrameWork / Library
  * Java 11 openjdk
  * SpringBoot 2.7.7
  * Jpa(Spring Data JPA)
  * QueyDsl

- #### Build tool
  * Gradle 7.6

- #### Emebedded Web Server
  * Apache Tomcat 9.0
  
- #### Database
  * MariaDB

### <b id="2.3"> DevOps </b>
- #### OS
  * Amazon Linux 2023 AMI

- #### Cloud Computing Service
  * Amazon EC2
  * Amazon RDS 
  * Amazon S3

- #### CI
  * GitHub Actions

- #### CD
  * AWS CodeDeploy
  
- #### 웹 서버 및 리버스 프록시
  * Nginx

### <b id="2.4"> 기타 주요 라이브러리 </b>
- Lombok
- websoket


## <b id="3"> 🔑 E-R 다이어그램 </b>
<img src="https://github.com/K-Y-k/KYKProject_FoodWorld_Spring/assets/102020649/af9b9267-1e1b-4c22-8394-68b9e874fcf4.png" width="800" height="500"/>


## <b id="4"> ⚙️ 아키텍처 구성 </b>


## <b id="5"> ▶️ 시연 영상 </b>
[![Video Label](http://img.youtube.com/vi/uZDP6DqmpI8/0.jpg)](https://youtu.be/uZDP6DqmpI8)


## <b id="6"> ✔️ 주요 기능 </b>
- ### 회원가입 / 유효성 검사
  - 닉네임, 아이디, 비밀번호는 기본적으로 글자 수 제한, 문자열 내 빈 공백 여부를 확인합니다.
  - 닉네임, 아이디는 추가로 중복의 유효성 검사가 진행되어야 회원가입이 됩니다.
    
    <img src="https://github.com/K-Y-k/KYKProject_FoodWorld_Spring/assets/102020649/538457da-0746-435f-b42c-1dcb3f415426.gif" width="50%"/>
    
 - ### 게시판 메인 페이징, 무한 스크롤 조회 / 파라미터 필터링 조회 / 검색
   - 자유/추천게시판의 메인은 페이징 처리를 하였고 검색한 파라미터에 따른 필터링 조회를 합니다.
   - 검색을 하면 키워드에 따른 게시글만 조회를 합니다.
  
     <img src="https://github.com/K-Y-k/KYKProject_FoodWorld_Spring/assets/102020649/8c3a5f1e-fc17-413d-b709-29bb35c0f44d.gif" width="50%"/>
     <img src="https://github.com/K-Y-k/KYKProject_FoodWorld_Spring/assets/102020649/a49f04e3-20a3-4121-b3b8-7269b910bd89.gif" width="50%"/>

   - 먹스타그램의 메인은 무한 스크롤로 페이징 하였습니다.
    
     <img src="https://github.com/K-Y-k/KYKProject_FoodWorld_Spring/assets/102020649/26be98d9-7e2e-45e3-9c44-9b3344034daa.gif" width="50%"/>
     
 - ### 게시글 등록, 파일 업로드
   - 게시글의 등록은 각 게시판에 맞는 구성과 유효성 검사를 적용하였고 다중 파일 업로드가 가능합니다.
  
     <img src="https://github.com/K-Y-k/KYKProject_FoodWorld_Spring/assets/102020649/2a6ed952-10c4-4775-bd7d-e933ed2bde39.gif" width="50%"/>

 - ### 게시글 상세 조회 / 좋아요 / 파일 다운로드
   - 등록한 게시글을 조회할 수 있고 좋아요, 파일 다운로드가 가능합니다.
   - 좋아요 기능은 각 게시글에 회원 당 1번만 적용이 가능하고 취소할 수도 있습니다.
    
     <img src="https://github.com/K-Y-k/KYKProject_FoodWorld_Spring/assets/102020649/cadb3a1b-2888-4b50-934a-aa7504ddb25d.gif" width="50%"/>
     
 - ### 게시글 수정 / 삭제
   - 게시글 수정은 일반 필드는 연관 편의 메서드 방식으로 수정하였고, 파일 부분은 기존 파일 삭제하고 새로 받은 파일로 재 생성합니다.
   - 게시글 삭제도 DB 삭제뿐만이 아닌 실제 파일도 찾아 삭제 처리합니다.
    
     <img src="https://github.com/K-Y-k/KYKProject_FoodWorld_Spring/assets/102020649/9fb75739-6071-4c37-ba65-e3581d85d1af.gif" width="50%"/>   
     
 - ### 댓글 조회 / 등록 / 수정 / 삭제
   - 댓글을 페이징 된 형식으로 조회하고 작성한 회원의 댓글을 수정, 삭제할 수 있습니다.
   - 댓글 수정 중에 다른 댓글로 수정하면 기존 수정 댓글 폼을 취소합니다.

     <img src="https://github.com/K-Y-k/KYKProject_FoodWorld_Spring/assets/102020649/2750055e-8404-4550-8d88-eb1fe98c6a7a" width="50%"/>   

 - ### 프로필 조회, 수정
   - 회원의 프로필을 조회할 수 있습니다. 현재 회원의 프로필일 경우 프로필 수정/회원 탈퇴를 할 수 있고
   - 수정의 유효성 검증은 회원가입과 동일합니다.

     <img src="https://github.com/K-Y-k/KYKProject_FoodWorld_Spring/assets/102020649/ec3a2dd6-afce-466f-8e82-17849966a83c" width="50%"/>   

  - ### 팔로우, 팔로우 추천 리스트
    - 다른 회원의 프로필일 경우 팔로우/언팔로우를 할 수 있고 팔로우를 하게 되면, 

      팔로우 추천 리스트에 팔로우한 회원을 팔로우한 회원들 또는 팔로우한 회원이 팔로우한 회원 기준으로 추천합니다.
     
      <img src="https://github.com/K-Y-k/KYKProject_FoodWorld_Spring/assets/102020649/65639e5e-6c08-4963-a56d-c1fa0c7eda15" width="50%"/>   

  - ### 메뉴 랜덤 추첨, CRUD
    - 모든 회원들이 등록했던 메뉴들 중 메뉴 카테고리를 선택하고 랜덤으로 추첨할 수 있습니다.
    - 메인은 현재 회원이 등록했던 메뉴들을 페이징 조회, 회원이 원하는 메뉴를 등록, 등록했던 메뉴를 수정/삭제할 수 있습니다. 
     
      <img src="https://github.com/K-Y-k/KYKProject_FoodWorld_Spring/assets/102020649/fc6748e3-f74c-44f6-82c1-0bb0e3b8a125" width="50%"/>   

  - ### 실시간 1 대 1 채팅
    - 상대 회원 프로필에서 채팅하기를 누르면 채팅방이 생성되어 실시간으로 채팅을 주고받을 수 있고 채팅 내역은 DB에 저장합니다. 
    - 1명의 유저가 채팅방을 퇴장한 후 다른 유저가 채팅방을 퇴장하지 않았으면 메시지 전송 시 퇴장한 유저를 다시 기존 채팅방으로 초대

      <img src="https://github.com/K-Y-k/KYKProject_FoodWorld_Spring/assets/102020649/93f693e1-a7e1-4c9f-8c2a-aa9dd0a63f6f" width="50%"/>   

  - ### Admin 페이지
    - 관리자 역할을 부여받은 회원은 회원, 게시판, 댓글, 메뉴, 채팅방, 채팅 메시지를 관리할 수 있습니다.

      <img src="https://github.com/K-Y-k/KYKProject_FoodWorld_Spring/assets/102020649/d14a7f9f-9cb7-450c-ae92-502a2e69e80c" width="50%"/>   
 

## <b id="7"> 🔥 어려웠던 점 및 극복 </b>
### 1. Slice 페이징의 한계
- Slice 페이징은 현재 받아온 엔티티의 id보다 <(작은 것)부터 가져와야 하는데 그렇게 되면 첫 페이지의 첫 데이터를 가져오지 못합니다. 그렇다고 <=(작거나 같음)으로 설정하면 끝의 데이터 후 다음 페이지에서 끝의 데이터가 한번 또 나오게 됩니다.
- 제가 생각한 방안은 첫 페이지인지의 여부를 파라미터로 설정해서 각 상황에 따른 첫 페이지 파라미터를 갱신해가며, 첫 페이지일 때는 <=, 첫 페이지가 아니면 <으로 모든 데이터를 가져올 수 있게 하였습니다.

### 2. JSON 양방향으로 매핑된 엔티티 필드의 무한 참조 발생
- Ajax로 API를 호출할 때 해당 조회하는 엔티티에 양방향으로 매핑된 엔티티가 있으면 서로 참조가 되어 무한 참조 문제가 발생합니다.
- 먹스타그램의 게시글 엔티티를 불러올 때는 회원 엔티티 필드의 정보가 필요가 없어 회원 엔티티를 @JsonIgnore로 간단히 방지할 수 있었지만, 회원 엔티티를 불러오는 관리자 페이지에서는 회원과 양방향으로 매핑된 게시글, 프로필, 메뉴, 채팅방 엔티티들의 정보가 필요하여 JSON을 호출할 때 DTO를 활용하여 필요한 API 스펙으로 변경하고 호출하였습니다.

### 3. 1 : N 관계에서의 leftJoin으로 중복 레코드 발생 
- 1 : 1, N : 1 관계에서는 leftJoin으로 row의 수가 틀어지지 않는데, 1 : N 관계에서는 중복 레코드가 발생하였습니다. 
- 1 : N 관계처럼 하나의 키에 여러 데이터가 있으면 그 데이터 수만큼의 row가 생겨 중복 레코드가 발생한 것입니다. 
- Distinct를 활용해 중복 레코드를 제거할 수 있지만, 사실 이 문제는 테이블 설계가 잘못된 경우가 아니라면 발생할 수 없는 문제였습니다.
  현재 필요한 Join인지 확인하였고 무분별한 Join을 사용하게 되어 발생했던 문제였습니다.

### 4. QueryDsl 랜덤 선택의 한계
- QueryDsl에서는 랜덤으로 select 하는 기능이 지원하지 않습니다. 
- 그래서 네이티브 SQL로 작성을 시도해봤지만 잘 되지 않아 대안책으로 QueryDsl로 관련 리스트를 가져오고 Java의 Random() 함수를 활용하여 해당 리스트 중에서 랜덤으로 선택하게 하였습니다.

### 5. 새로운 기술 습득 및 극복
- 처음으로 접해보는 기술로 실시간 채팅에서 사용되는 Websocket, Stomp의 원리를 이해는 했지만 서핑하여 얻은 코드의 정보가 이해가 가지 않았습니다. 해당 코드 정보는 실시간 채팅이 주였고 저는 DB에 채팅 내역을 저장 및 출력까지 구현해야했기에 사이클을 명확히 이해했어야 했습니다.
- 하지만 계속 코드를 반복적으로 리뷰하고 소켓 연결 후 채팅방 생성, 사용자 입장/대화/퇴장(소켓 끊기)의 경우에 따른 사이클 흐름을 따라가는 과정을 통해 코드가 읽혀짐으로써 제가 구현하고 싶었던 프로젝트에 맞춰 적용시켰습니다. 


## <b id="8"> 💡 프로젝트를 통해 느낀점 </b>
- 혼자서 기획, 설계, 백엔드/프론트 개발, 배포 모든 과정을 거치면서 <b>개발의 전체적인 라이프 사이클을 파악</b>할 수 있었음.
- 기본적인 <b>엔티티 설계 및 관계 매핑, 리포지토리/서비스/컨트롤러/뷰의 계층과 MVC 구조에 적응</b>하였고 <b>쿼리 파라미터의 전송, 전달에 능숙</b>해짐.
- 기능을 구현할 때 발생할 수 있는 <b>모든 테스트 케이스들을 면밀히 분석해야함</b>.
- 기능을 구현할 때 필요한 쿼리 파악과 쿼리를 구현하기 위한 <b>JPQL, SpringDataJpa, QueryDsl 기술중 적절한 선택 및 구현</b>이 가능해짐.
- Ajax와 JSON의 동작 원리를 알고, <b>Ajax로 JSON호출 및 응답 방법</b>을 알게 되었음.
- WebSocket의 동작 원리를 알고, <b>WebSocket 실시간 채팅 방법</b>을 알게 되었음.
- 구현 중 막히는 상황이 많았지만 그런 상황속에도 <b>포기하지 않고 끝까지 몰두하는 과정을 통해 문제해결 능력을 기를 수 있었음</b>.
- AWS 환경으로 마이그레이션을 하고 서비스를 CI/CD 파이프라인을 통해 배포하면서 <b>AWS의 각 서비스들을 어떤식으로 사용하고 효율성을 올려주는 자동화의 필요성을 알 수 있었음</b>.


## <b id="9"> 📒 프로젝트 관련 추가 포스팅 </b>
- 🔗 <a href="https://blog.naver.com/kyk7777_/222975254105" target="_blank">프로젝트 명세서</a>
- 🔗 [구조 설계 / 오라클 서버 세팅 / 스프링 세팅](https://blog.naver.com/kyk7777_/222975254105)
- 🔗 [회원 가입 / 로그인, 로그아웃 / 유효성 검사 / 회원 탈퇴](https://blog.naver.com/kyk7777_/222978032496)
- 🔗 [게시판 CRUD / 조회수 증가](https://blog.naver.com/kyk7777_/222979729796)
- 🔗 [페이징 / 검색](https://blog.naver.com/kyk7777_/222980904399)
- 🔗 [좋아요](https://blog.naver.com/kyk7777_/222988457441)
- 🔗 [댓글 CRUD / 페이징](https://blog.naver.com/kyk7777_/222989407593)
- 🔗 [파일 업로드](https://blog.naver.com/kyk7777_/222992202791)
- 🔗 [파일 다운로드](https://blog.naver.com/kyk7777_/222993172577)
- 🔗 [먹스타그램 무한 스크롤 페이징 / 검색 / 좋아요 (Ajax와 JSON 활용)](https://blog.naver.com/kyk7777_/223028739381)
- 🔗 [먹스타그램 무한 스크롤 문제 및 해결](https://blog.naver.com/kyk7777_/223032104975)
- 🔗 [프로필 조회 / 수정](https://blog.naver.com/kyk7777_/223033274407)
- 🔗 [팔로우/언팔로우, 팔로워 리스트, 팔로워 연관 회원 추천 리스트](https://blog.naver.com/kyk7777_/223035839069)
- 🔗 [소켓 통신 1 대 1 채팅방](https://blog.naver.com/kyk7777_/223039140976)
- 🔗 [메뉴 랜덤 추첨](https://blog.naver.com/kyk7777_/223058461117)
- 🔗 [메인 페이지, 인기글](https://blog.naver.com/kyk7777_/223067248635)
- 🔗 [추천 게시판 카테고리별 필터링 조회](https://blog.naver.com/kyk7777_/223075193623)
- 🔗 [Admin 페이지](https://blog.naver.com/kyk7777_/223089039632)
- 🔗 [AWS EC2 서버 환경 만들기](https://blog.naver.com/kyk7777_/223125604358)
- 🔗 [EC2 서버에 접속하기](https://blog.naver.com/kyk7777_/223125973886)
- 🔗 [AWS RDS 환경 만들기](https://blog.naver.com/kyk7777_/223126310297)
- 🔗 [EC2 서버에 프로젝트 배포하기](https://blog.naver.com/kyk7777_/223127302171)
- 🔗 [GitHub Action CI 배포 자동화](https://blog.naver.com/kyk7777_/223129233019)
- 🔗 [무중단 배포(Nginx)](https://blog.naver.com/kyk7777_/223131110589)
- 🔗 [S3 연동한 환경에서의 파일 업로드 / 불러오기 / 삭제 / 다운로드](https://blog.naver.com/kyk7777_/223201565729)
