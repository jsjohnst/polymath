#!/usr/bin/env ruby

require 'term/ansicolor'
include Term::ANSIColor

 p red, "Hello", blue, "World"

print "33[32m Connecting"
# Actual work
puts "Importing categories [ e[32mDONEe[0m ]"
# Actual work
puts "Importing tags       [e[31mFAILEDe[0m]"

arr = [1,2,1,2,1,1,0, 1,1,2,0,1,1,0,1,1,0,1,1, 0,0,0,0,0,0, 0,0,0,0,0,0, 0,0,0,0,0,0]
arr = [0,1,1,1,1,1,1,2,2,2,2,2,2,2,2,2,2,2,2, 3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3]
arr.length

(arr.length - 1) /6


N = 12
grid = Array.new(N)
for i in 0..grid.length do
  grid[i]=Array.new(N," ")
end
center = N/2

# positions on grid when running through it in circles
i=center # vertical index
j=center # horizontal index
grid[i][j] = arr.shift

for currentRing in 1..3 do 
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
