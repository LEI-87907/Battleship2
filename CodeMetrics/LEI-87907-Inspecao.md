# Inspeção de código – Ficha 3

O Inspect Code deu quase 1600 avisos, mas a maioria vem do Javadoc da pasta `docs` e do corretor ortográfico em inglês. Os mais importantes são os 14 de Security no `pom.xml`: dependências do Spring Boot com vulnerabilidades conhecidas.

O Qodana deu 23 problemas, todos High, porque o `qodana.yaml` usa o perfil `qodana.starter`, que só tem as inspeções principais. Nenhum está no código do meu requisito. O aviso de busy wait no `Tasks` parece-me um falso positivo, porque o `Thread.sleep` do `simula` é de propósito.

Ao rever o código da exportação para PDF criei as issues #3 a #7.
