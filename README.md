# FilePart
cross-platform file partitioner made in java

## Struttura del software
FilePart è stato sviluppato seguendo il pattern MVC. Il model `AppModel` si occupa di tutta la parte di elaborazione dei dati ed è completamente agnostico rispetto al resto del programma. 
Per quanto riguarda la gestione dei dati strettamente collegati alla GUI (ad esempio il `FileTableModel`) viene utilizzato un altro model, chiamato `GUIModel` che estende `AppModel`.
La view `AppView` espone al controller i metodi per leggere e manipolare i dati al suo interno e aggiungere listener ai suoi componenti. Buona parte della view è riutilizzabile in altri progetti simili.
Il controller `AppController` viene istanziato passandogli model e view, definisce il comportamento dei componenti della view e richiama i metodi esposti dal model per eseguire tutte le operazioni sui dati richieste dall'utente.
