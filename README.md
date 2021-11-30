# project-multithread

## Zadanie

Vytvorte klient-server aplikáciu na kopírovanie adresára. Požadované vlastnosti:

- Sťahovanie adresára (stromu adresárov a súborov) prebieha paralelne cez používateľom daný počet TCP soketov.
- Kopírovanie prebieha iba zo servra na klient
- Jeden súbor sa presúva vždy len cez jeden soket, po jeho prenesení posiela tento soket ďalší súbor, ak ešte je ďalší, čo treba posielať
- Toto sťahovanie je prerušiteľné tak, že sa vypne server (simulácia straty spojenia) alebo klient (simulácia toho, že používateľ musí nečakane vypnúť/reštartovať počítač). Po opätovnom nadviazaní spojenia medzi serverom a klientom, má klient možnosť pokračovať v dokopírovaní od momentu prerušenia (už stiahnuté časti súborov sa neťahajú znova) opäť paralelne cez daný počet TCP soketov.
- Grafické používateľské rozhranie pre klienta vo frameworku JavaFX obsahujúce aspoň
  - progressbar znázorňujúci percento skopírovania počtu súborov
  - progressbar znázorňujúci percento skopírovanej veľkosti dát v MB
  - tlačidlo na začatie kopírovania
  - tlačidlo na opätovné pokračovanie v kopírovaní, ak je (pri spustení) zistené, že kopírovanie bolo prerušené
- Nie je potrebné programovať prehľadávač disku „na druhej strane“ na výber adresára ani výber cieľového umiestnenia adresára
- Odporúčam nepoužívať vytváranie špeciálnych paketov s hlavičkami, ale posielať cez Socket.getOutputStream().write() po prípadných úvodných dohodách iba dáta. Uzatvorenie streamu sa dá odchytiť cez výnikmku IOException, keď sa zatvorí socket.
- Program má byť schopný skopírovať bez problémov na lokálnej sieti, alebo v rámci localhostu aj 1GB stredne veľkých súborov pod 1 minútu
Požadované vlastnosti
  - projekt musí využívať na správu vlákien Executor – nevytvárate vlastné Thread-y
  - použite aspoň jeden synchronizér
  - odchyťte v úlohe udalosť prerušenia
  - počet dokopy vytvorených TCP spojení počas celej doby kopírovania musí byť rovný počtu TCP Soketov zadaných používateľom s prípadným bonusovým jedným TCP soketom/spojením na manažovanie kopírovania (ak máte potrebu ho použiť) – teda, počas kopírovania žiadne nové TCP spojenia nevznikajú
Zakázané vlastnosti
  - použitie uspatia vlákna na určitý čas namiesto vhodného sychronizéra
