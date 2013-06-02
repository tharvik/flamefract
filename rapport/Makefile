# First Makefile of my life

rapport : rapport.tex
	latex rapport.tex
	dvipdf rapport.dvi
	rm rapport.aux
	rm rapport.out
	rm rapport.dvi
	rm rapport.log


.PHONY : clean
clean : 
	rm rapport.aux
	rm rapport.out
	rm rapport.dvi
	rm rapport.log
