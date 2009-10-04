require "hexagon.rb"
require "rubygems"
require 'term/ansicolor'
include Term::ANSIColor

def print_array(rings, state)
arr=state  
tmp = Array.new
for i in 0..arr.length-1 do 
  if arr[i] == 1
    tmp[i] = bold, green, "+", reset 
  elsif arr[i] == 2 
    tmp[i] = bold, red, "+", reset 
  else
    tmp[i] = " "
  end
end

arr=tmp


n = rings*6+3
grid = Array.new(n)
for i in 0..grid.length do
  grid[i]=Array.new(n," ")
end
center = n/2

# positions on grid when running through it in circles
i=center # vertical index
j=center # horizontal index
grid[i][j] = arr.shift

for currentRing in 1..rings do 
  i = center-2*currentRing
  j = center
  # up and direction 0
  # we start above the center and go down diagonally
  for k in 1..(currentRing) do
    grid[i][j] = arr.shift
    i += 1
    j += 1
  end
  # direction 2
  # we go straight down on the right side
  for k in 1..(currentRing) do 
    grid[i][j]= arr.shift
    i += 2
  end
  # direction 3
  for k in 1..(currentRing) do 
    grid[i][j]= arr.shift
    i += 1 
    j -= 1 
  end

  # direction 4
  for k in 1..(currentRing) do 
    grid[i][j]= arr.shift
    i -= 1 
    j -= 1 
  end
  # direction 5, straight up
  for k in 1..(currentRing) do 
    grid[i][j]= arr.shift
    i -= 2 
  end
  # direction 4
  for k in 1..(currentRing) do 
    grid[i][j]= arr.shift
    i -= 1 
    j += 1 
  end
end

for i in 0..grid.size-1 do 
  for j in 0..grid.size-1 do 
    print "#{grid[i][j]} "
  end
  puts
end

puts state.to_s
puts

end

foo = [1,1,0,0,2,2,1,1]
bar = Grid.new(foo)

for i in 1..10
	print_array bar.getRingCount, bar.getCurrentState
	bar = bar.getIteratedGrid
end
