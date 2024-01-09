## Gera chaves utilizando RSA-256
```shell
keytool -genkeypair -alias algafood -keyalg RSA -keypass 123456 -keystore algafood.jks -storepass 123456
```
## Mostra as chaves a partir de um arquivo
```shell
keytool -list -keystore .\algafood.jks
```
