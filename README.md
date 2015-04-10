# TetrisAI
Create AI for Tetris game by applying machine learning algorithm

To visualize:

java PlayerSkeleton < weight.txt

To train: (better giving a base weight as the starting point first)
java Training

Weight is stored in weight.txt

Weight parameters:
1st: constant weight,
2nd: weight for height of the columns,
3rd: weight for the difference between two consecutive columns,
4th: weight for the maximum of height,
5th: weight for number of hole.

