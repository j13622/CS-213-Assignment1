This project is Chess, for Software Methodology class (CS 213 at Rutgers). It runs in the shell. You can run the program using "java chess/PlayChess". The actual back-end is in chess/Chess.java. This project fully implements STALEMATE, CHECKMATE, CHECK, ILLEGAL MOVES, EN PASSANT, DRAWS, and RESIGNATIONS.

Here is a sample run of PlayChess:

    e2 e4

    bR bN bB bQ bK bB bN bR 8
    bp bp bp bp bp bp bp bp 7
       ##    ##    ##    ## 6
    ##    ##    ##    ##    5
       ##    ## wp ##    ## 4
    ##    ##    ##    ##    3
    wp wp wp wp    wp wp wp 2
    wR wN wB wQ wK wB wN wR 1
     a  b  c  d  e  f  g  h

    g8 h6

    bR bN bB bQ bK bB    bR 8
    bp bp bp bp bp bp bp bp 7
       ##    ##    ##    bN 6
    ##    ##    ##    ##    5
       ##    ## wp ##    ## 4
    ##    ##    ##    ##    3
    wp wp wp wp    wp wp wp 2
    wR wN wB wQ wK wB wN wR 1
     a  b  c  d  e  f  g  h

    e4 e6

    ILLEGAL_MOVE

    bR bN bB bQ bK bB    bR 8
    bp bp bp bp bp bp bp bp 7
       ##    ##    ##    bN 6
    ##    ##    ##    ##    5
       ##    ## wp ##    ## 4
    ##    ##    ##    ##    3
    wp wp wp wp    wp wp wp 2
    wR wN wB wQ wK wB wN wR 1
     a  b  c  d  e  f  g  h

    quit

