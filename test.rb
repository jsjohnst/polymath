require "hexagon.rb"
foo = [1,1,1,1,1,1,1,1]
bar = Grid.new(foo)

for i in 1..10
	puts bar.getCurrentState.to_s + "\n\n"
	bar = bar.getIteratedGrid
end
