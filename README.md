# FilePart
cross-platform file partitioner made in java

## Struttura del software
FilePart è stato sviluppato seguendo il pattern MVC. Il model `AppModel` si occupa di tutta la parte di elaborazione dei dati ed è completamente agnostico rispetto al resto del programma. 

Per quanto riguarda la gestione dei dati strettamente collegati alla GUI (ad esempio il `FileTableModel`) viene utilizzato un altro model, chiamato `GUIModel` che estende `AppModel`.

La view `AppView` espone al controller i metodi per leggere e manipolare i dati al suo interno e aggiungere listener ai suoi componenti. Buona parte della view è riutilizzabile in altri progetti simili.

Il controller `AppController` viene istanziato passandogli model e view, definisce il comportamento dei componenti della view e richiama i metodi esposti dal model per eseguire tutte le operazioni sui dati richieste dall'utente.

### Ereditarietà e Polimorfismo
`FileAction` è l'interfaccia implementata da tutte le classi "azione", ovvero le classi che si occupano di dividere/ricomporre/cifrare un file.

`FileConfiguration` è la classe che descrive lo stato dell'azione: il file, il `ProcessStatus`, e la `SplitConfiguration`.

La `SplitConfiguration` è la classe che contiene la lista di opzioni che servono a identificare la `FileAction` corretta da utilizzare.

In breve: 
- `FileConfiguration` = Su cosa effettuare la `FileAction`
- `SplitConfiguration` = Come effettuare la `FileAction`

Le `FileAction` hanno la seguente gerarchia:
- `FileMerger`
- `FileSplitterByMaxSize`
  - `FileSplitterByPartNumber`
- <`FileCipher`>
  - `FileEncryptor`
  - `FileDecryptor`
- `FileMergeAndDecrypt`
- `FileSplitAndEncrypt`

`FileSplitterByPartNumber` estende `FileSplitterByMaxSize`, utilizza una formula per calcolare la "max size" dei file in modo da averne il numero richiesto, poi utilizza i metodi della classe padre per la divisione.

<`FileCipher`> è abstract e non implementa `FileAction`, contiene i metodi per la cifratura dei file.

`FileMergeAndDecrypt` e `FileSplitAndEncrypt` sono delle `FileAction` che utilizzano altre `FileAction` al loro interno.
