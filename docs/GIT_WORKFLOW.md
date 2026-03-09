# Git Workflow (main, eike, georg)

See dokument kirjeldab, kuidas töötada Gitiga projektis, kus kasutatakse
kolme branchi:

-   `main` -- stabiilne põhikood
-   `eike` -- Eike arenduse branch
-   `georg` -- Georgi arenduse branch

Eesmärk on, et igapäevane arendus toimuks eraldi branchides ning `main`
sisaldaks ainult töötavat ja kontrollitud koodi.

------------------------------------------------------------------------

# Branchide rollid

## main

-   sisaldab stabiilset koodi
-   siit tehakse vajadusel deploy
-   sinna merge'itakse ainult valmis töö

## eike

-   Eike arenduse branch
-   sinna tehakse commitid arenduse käigus

## georg

-   Georgi arenduse branch
-   sinna tehakse commitid arenduse käigus

------------------------------------------------------------------------

# Üldine struktuur

                     main
    ------------------o------------------o------------------->
                      \                  /
                       \                /
                        \    merge     /
                         \            /

    eike        ----------o----o----o------------------------>
                          commit commit push

    georg       ---------------o----o------------------------>
                                commit push

Näide commitite ajaloost:

    A---B----------------------C------------------------ main
         \                    /
          \--D---E---F-------/                         eike
           \
            \----G---H-------------------------------- georg

------------------------------------------------------------------------

# Git käskude tähendus

## commit

`commit` salvestab lokaalsed muudatused Git ajaloosse.

Commit toimub ainult sinu arvutis kuni sa `push` ei tee.

Commit tee siis kui said ühe loogilise tööetapi valmis.

Näited: - uus funktsioon - bugfix - refactor - väiksem valmis tööosa

``` bash
git add .
git commit -m "Lisa kasutaja profiili kontroll"
```

------------------------------------------------------------------------

## push

`push` saadab commitid serverisse (remote repository).

Pärast push'i näevad teised ka sinu muudatusi.

Kasuta push'i kui: - tahad muudatused serverisse salvestada - tahad tööd
jagada - lõpetad tööpäeva

``` bash
git push origin eike
```

------------------------------------------------------------------------

## pull

`pull` toob serverist uuemad muudatused sinu lokaalsesse reposse.

Kasuta pull'i kui: - alustad tööd - enne merge'i - keegi teine on
branchi muutnud

``` bash
git pull origin main
```

või

``` bash
git pull origin eike
```

------------------------------------------------------------------------

## merge

`merge` ühendab ühe branchi muudatused teise branchi.

Näiteks:

-   `main` → `eike`
-   `eike` → `main`
-   `georg` → `main`

Näide:

``` bash
git checkout main
git merge eike
```

------------------------------------------------------------------------

# Tavaline töövoog

## 1. Päeva alustamine

Too uusim seis:

``` bash
git checkout main
git pull origin main

git checkout eike
git pull origin eike
```

------------------------------------------------------------------------

## 2. Töö tegemine oma branchis

Mine oma branchi:

``` bash
git checkout eike
```

Tee muudatused.

Kui üks loogiline osa on valmis:

``` bash
git add .
git commit -m "Lisa profiilivaate backend"
```

------------------------------------------------------------------------

## 3. Muudatuste serverisse saatmine

``` bash
git push origin eike
```

Soovitav on push'ida regulaarselt.

------------------------------------------------------------------------

## 4. Kui main on muutunud

Kui keegi teine on juba `main` branchi muutnud, tuleks see tuua oma
branchi.

``` bash
git checkout main
git pull origin main

git checkout eike
git merge main
```

Kui tekivad konfliktid, lahenda need oma branchis, mitte `main` peal.

------------------------------------------------------------------------

## 5. Kui töö on valmis

Kui funktsioon töötab ja kood on kontrollitud, saab selle viia `main`
branchi.

``` bash
git checkout main
git pull origin main

git merge eike
git push origin main
```

Pärast seda on muudatused `main` branchis.

------------------------------------------------------------------------

# Olulised reeglid

## 1. Ära tööta otse main branchis

Arendus toimub alati: - `eike` - `georg`

## 2. Tee commitid loogiliste sammudena

Hea commit = üks arusaadav muudatus.

## 3. Push regulaarselt

Push aitab: - vältida töö kaotamist - jagada muudatusi

## 4. Enne merge'i värskenda main

``` bash
git checkout main
git pull origin main
```

## 5. Main peab jääma stabiilseks

`main` ei tohiks sisaldada:

-   poolikut koodi
-   katsetusi
-   katkist buildi
