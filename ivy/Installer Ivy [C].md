Ouvrir un nouveau terminal



````shell script
cd c/lib/
unzip ivy.zip
unzip pcre-7-7.zip
````

Utiliser les commandes suivantes : compiler la librairie PCRE puis ivy

`````shell  script
cd pcre-7-7
chmod 777 configure && ./configure
sudo make
sudo make install
export LD_LIBRARY_PATH=/lib:/usr/lib:/usr/local/lib
cp .libs/libpcre.a ../ivy && cd ../ivy
make
`````

Vous pouvez maintenant essayer l'outil *ivyprobe*
````shell script
./ivyprobe "(.*)"
````

Recuperer les librairies Ivy et PCRE.
````shell script
mv *.a ../../../../impeesa/lib
````

