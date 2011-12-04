#!/bin/bash
file=$(find . -maxdepth 1 | dmenu -l 30 -b)
Xdialog --title "EDIT BOX" --fixed-font --print "" --editbox ~/temp.txt 24 75 2 >> ~/temp_2.txt
exit 0;



































case $? in
  0)
    cat ./temp.txt
    echo "OK"
    ;;
  1)
    echo "Cancel pressed."
    ;;
  255)
    echo "Box closed."
    ;;
esac

exit 0;


comentario=$(cat ./temp.txt)
[ -z "$comentario" ] && {
	echo -e "Nada que hacer...\nSaliendo...: [$comentario]"
	exit 0;
}
git add "$file";
echo -e "git add $file";
git branch;
echo -e "git branch";
git commit -m "$comentario";
echo -e "git commit -m \"$comentario\"";
git push -u origin master
echo -e "git push -u origin master";
echo -e "exit...";
