# IntelliJ에서 deploy.sh 사용법

## Windows

- **Shell Script를 실행할 수 있는 Git Bash나 Zsh의 설치가 필요합니다.**
- `Script path`에 deploy.sh의 절대 경로를 지정합니다. Windows의 IntelliJ는 상대 경로를 지원하지 않습니다.
- `Script options`에 `.jar` 파일이 이동할 절대 경로를 지정합니다.
- `Before launch` 옵션에서 Gradle의 Jar Build 작업을 추가합니다.
  - 혹은 그 밖에 `.jar` 파일 빌드 옵션을 선택할 수 있습니다.

![image1](/images/screenshot1.png)

- 추가된 작업으로 `.jar` 파일을 원하는 곳에 옮길 수 있습니다. 

![image2](/images/screenshot2.png)


## Linux/Mac

- 향후 추가 예정
