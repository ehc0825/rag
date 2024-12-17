# rag-sdk
- java 17
- spring boot 3.1.2
  
## Moduels  
  
### 1.common
- 공통으로 사용되는 코드를 모아놓은 모듈입니다.  
  
#### dependencies
- kr.co.mayfarm.filter
- com.fasterxml.jackson.core jackson-databind 2.17.1
  
### 2.ollama
- Mayfarm ollama Model을 사용하기 위한 모듈입니다.
- common 모듈을 사용합니다.
  
#### dependencies
- com.fasterxml.jackson.core jackson-databind 2.17.1
- common module
  
### 3.rag-core
- rag를 사용하기 위한 모듈입니다.
- semantic-search 모듈을 사용합니다.
- common 모듈을 사용합니다.
- ollama 모듈을 사용합니다.
  
#### dependencies
- com.fasterxml.jackson.core jackson-databind 2.17.1
- org.opensearch.client java-client 2.13.1-SNAPSHOT
- org.apache.httpcomponents.client5 httpclient5 5.2.1
- common module
- ollama module
- semantic-search module
  
### 4.semantic-search
- semantic search를 사용하기 위한 모듈입니다.
- common 모듈을 사용합니다.
  
#### dependencies
- org.opensearch.client java-client 2.13.1-SNAPSHOT
- org.apache.httpcomponents.client5 httpclient5 5.2.1
- com.fasterxml.jackson.core jackson-databind 2.17.1
- common module
  

  
