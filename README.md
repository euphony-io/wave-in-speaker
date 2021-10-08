# check-in-speaker

<p align='center'><img src="https://user-images.githubusercontent.com/58473522/136589812-bcd3f6e7-d74b-4758-9f07-a991129b4fa7.png"  width="150" height="150"/></p>

check-in-speaker는 방문하는 공간에 전자출입명부를 쉽게 남기도록 도와주는 앱이며 특히 코로나 바이러스 감염증(COVID-19) 기간동안 효과적으로 사용가능합니다.

이 앱을 사용하면 QR코드 대신 음파 통신으로 체크인할 수 있습니다.

체크인이 완료되면 체크인 시간과 함께 방문 장소가 데이터베이스에 저장되어 방문 기록을 확인할 수 있습니다.

이 앱은 [check-in-listener](https://github.com/euphony-io/check-in-listener) 앱과 함께 사용됩니다.

## 이 앱이 만들어진 배경

기존 'QR코드 체크인'은 다음과 같은 문제점이 있습니다.

* 데이터를 수신하는 기기의 렌즈가 더러우면 잘 인식되지 않습니다.
* 주변이 너무 밝거나 너무 어두우면 잘 인식되지 않습니다.
* 특히 어린아이와 노인의 경우 QR코드에 액세스하는 과정이 복잡합니다.

이 앱은 사용자 친화적이며 음파 통신을 사용하기 때문에 위와 같은 불편함이 없습니다!

또한 휴대폰번호와 같이 민감한 개인정보를 대체해 수기명부에 기록할 수 있는 '개인 안심번호'가 유명무실한 현 상황에서, 이 앱은 '개인 안심번호'를 효과적으로 활용하는 수단이 될 수 있습니다.

\* 개인 안심번호는 네이버와 카카오, PASS QR체크인 화면에서 확인가능합니다.

## 앱 실행 방법

<img src="https://user-images.githubusercontent.com/58473522/136418521-673ccc90-9398-4c2f-be9a-6842cd95233a.png"  width="250" height="480"/>  <img src="https://user-images.githubusercontent.com/58473522/136419317-5b4d40d8-2be1-41dc-b3c7-efbe8ca96c76.png"  width="250" height="480"/>

1. 첫 실행 시 위치 정보 수집을 허용하고 listener 앱으로 음파 통신을 보내는 데에 사용되는 안심번호를 입력받습니다.
<br>위치 정보는 추후 방문 기록을 남기는 데에 사용됩니다.

<img src="https://user-images.githubusercontent.com/58473522/136419505-2a0c0d9a-9b3a-4499-92a9-e5af63fe998c.png"  width="250" height="480"/>  <img src="https://user-images.githubusercontent.com/58473522/136421998-5f741d67-39ca-4062-8595-42abe29f75db.png"  width="250" height="480"/>

2. listener 앱이 실행되고 있을 때 `체크인 시작` 버튼을 누릅니다.
<br>체크인이 진행되는 동안 로딩다이얼로그가 활성화되며, 음파 통신을 위해 실행 중이던 음악 등을 중지하고 볼륨이 자동으로 키워집니다.
<br>`체크인 종료` 버튼을 통해 언제든지 음파 통신을 멈출 수 있습니다.

<img src="https://user-images.githubusercontent.com/58473522/136419533-dbc8f7c9-90d8-4652-87a2-fa5e7c34d8ac.png"  width="250" height="480"/>

3. `방문 기록 조회` 버튼을 통해 '방문 기록' 페이지에 진입합니다.
<br>자신이 체크인한 시간과 매장의 위치를 최신 순으로 확인 가능합니다.

## Issues 리포트

문제점이 발견되거나 새로운 기능을 요청하고 싶으면 [여기에서 이슈를 작성해주세요](https://github.com/euphony-io/check-in-speaker/issues/new/choose). 

## License

check-in-speaker는 Apache 2.0 license를 따릅니다. 자세한 내용은 [LICENSE](https://github.com/euphony-io/check-in-speaker/blob/master/LICENSE)를 참조하세요.


