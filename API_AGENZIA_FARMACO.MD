cerca_farmaco
```
https://www.agenziafarmaco.gov.it/services/search/select?fl=sm_field_codice_farmaco,sm_field_descrizione_farmaco,sm_field_descrizione_ditta,sm_field_tipo_procedura&q=bundle:confezione_farmaco+sm_field_descrizione_farmaco:" + farmaco + "*&df=sm_field_descrizione_farmaco&wt=json&rows=150000
```
carica_dati_farmaco
```
https://www.agenziafarmaco.gov.it/services/search/select?q=bundle:confezione_farmaco+sm_field_codice_farmaco:' + codice_farmaco + '&df=sm_field_codice_farmaco&wt=json&rows=150000
```
cerca_azienda
```
https://www.agenziafarmaco.gov.it/services/search/select?fl=sm_field_descrizione_ditta,sm_field_codice_ditta&q=bundle:confezione_farmaco+sm_field_descrizione_ditta:' + search + '*&df=sm_field_descrizione_ditta&wt=json&rows=150000
```
```
https://www.agenziafarmaco.gov.it/services/search/select?fl=sm_field_codice_farmaco,sm_field_descrizione_farmaco,sm_field_descrizione_ditta&q=bundle:confezione_farmaco+sm_field_codice_ditta:*" + azienda + "*&df=sm_field_codice_ditta&wt=json&rows=150000
```
cerca_princ_att
```
princ_att= princ_att.replace(/%27/g, "*");
princ_att= princ_att.replace(/%20/g, " ");
princ_att = princ_att.replace(/\(|\)/g, "*");
```
```
https://www.agenziafarmaco.gov.it/services/search/select?fl=sm_field_codice_farmaco,sm_field_descrizione_farmaco,sm_field_descrizione_ditta,sm_field_descrizione_atc,sm_field_codice_atc&q=bundle:confezione_farmaco+sm_field_descrizione_atc:' + search + '*&df=sm_field_descrizione_atc&wt=json&rows=150000
```
```
https://www.agenziafarmaco.gov.it/services/search/select?fl=sm_field_codice_farmaco,sm_field_descrizione_farmaco,sm_field_descrizione_ditta,sm_field_descrizione_atc&q=bundle:confezione_farmaco+sm_field_descrizione_atc:" + princ_att + "&df=sm_field_descrizione_atc&wt=json&rows=150000
```