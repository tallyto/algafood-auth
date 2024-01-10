## Gera chaves utilizando RSA-256
```shell
keytool -genkeypair -alias algafood -keyalg RSA -keypass 123456 -keystore algafood.jks -storepass 123456
```
## Mostra as chaves a partir de um arquivo
```shell
keytool -list -keystore .\algafood.jks
```
## Exporta o certificado a partir do arquivo 
```shell
 keytool -export -rfc -alias algafood -keystore algafood.jks -file algafood-cert.pem
```
## Gera a chave publica a partir do certificado
```shell
openssl x509 -pubkey -noout -in .\algafood-cert.pem > algafood-pkey.pem
```