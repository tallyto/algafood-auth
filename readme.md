# Algafood Auth

## Descrição
O **Algafood Auth** é o Authorization Server do sistema Algafood. Essa aplicação é responsável por gerenciar a autenticação e autorização de usuários, fornecendo tokens de acesso seguro para serem utilizados em outros serviços da plataforma.

## Versão
A versão atual do projeto é **0.0.1-SNAPSHOT**.

## Pré-requisitos
- [Java](https://www.oracle.com/java/technologies/javase-downloads.html) (Versão 17)
- [Maven](https://maven.apache.org/download.cgi)

## Dependências
O projeto utiliza o **Spring Boot** na versão **2.7.16** como parent e inclui as seguintes dependências:

- **spring-boot-starter-web**: Oferece suporte para a construção de aplicativos da web usando o Spring MVC.
- **spring-boot-devtools**: Fornece ferramentas de desenvolvimento, como reinicialização automática do aplicativo.
- **spring-security-oauth2-autoconfigure**: Configuração automática para o Spring Security OAuth2, versão **2.2.3.RELEASE**.

## Configuração
O projeto utiliza o plugin **spring-boot-maven-plugin** para facilitar a construção e execução. Certifique-se de ter o Maven instalado e execute o seguinte comando para iniciar a aplicação:

```bash
mvn spring-boot:run
```