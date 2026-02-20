build:
	javac src/main/java/*.java -d bin

run: build
	java -cp bin MazeManager --compare BFSSolver DFSSolver