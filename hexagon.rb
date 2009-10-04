class Grid
	attr_accessor :rings

	def initialize(initialState)
		@rings = Array.new
		
		if initialState.length
			@rings[0] = Array.new
			@rings[0].push Cell.new(initialState.shift) 
			
			currentRing = 0
			currentRingSize = currentRing * 6
			currentRingPos = 0
			
			while initialState.length > 0
				if currentRingPos >= currentRingSize
					currentRing = currentRing + 1
					currentRingSize = currentRing * 6
					currentRingPos = -1 # this will get incremented to 0 below 
					@rings[currentRing] = Array.new
				end
	
				currentRingPos = currentRingPos +1
				
				state = initialState.shift
				@rings[currentRing][currentRingPos] = Cell.new(state)
			end

			if currentRingPos < currentRingSize
				for i in currentRingPos..(currentRingSize-1)
					@rings[currentRing][i] = Cell.new(-1)
				end
			end
		end
	end

	def getCurrentState
		state = Array.new
		for i in 0..@rings.length-1
			for j in 0..@rings[i].length-1
				state.push @rings[i][j].state
			end
		end
		state
	end
	
	def newCellState(currentState, neighbors)
	  random = rand(7)
	  if random == 6 
	    return currentState
	  else
	    states = Array.new
	    for i in 0..neighbors.length-1
	      neighbor = neighbors[i]
	      if neighbor[0] < @rings.length && neighbor[1] < @rings[neighbor[0]].length
  	      if @rings[neighbor[0]][neighbor[1]].state > 0
  	        # making virus infected states twice as likely to be picked
  	        states.push @rings[neighbor[0]][neighbor[1]].state
  	        states.push @rings[neighbor[0]][neighbor[1]].state
  	      else
  	        states.push @rings[neighbor[0]][neighbor[1]].state
  	      end
  	    end
	    end
	    
	    return states[rand(states.length)]
	  end
	end

	def getIteratedGrid
		newGrid = self.clone
		newGrid.rings[newGrid.rings.length] = Array.new
		for pos in 0..(newGrid.rings.length-1)*6
			newGrid.rings[newGrid.rings.length-1][pos] = Cell.new(-1)
		end
		for i in 0..newGrid.rings.length - 1
			for j in 0..newGrid.rings[i].length - 1
				neighbors = findCellNeighbors(i,j)
				
				newState = newCellState(@rings[i][j].state, neighbors)
				
				if(newState >= 0)
					newGrid.rings[i][j].state = newState
				end
			end
		end
		ringCount = newGrid.rings.length-1
		for i in 0..ringCount
				if i >= newGrid.rings.length
					break
				end
				allNonInfected = true
				for j in 0..newGrid.rings[i].length-1
					if newGrid.rings[i][j].state > 0 
						allNonInfected = false
					end
					if newGrid.rings[i][j].state < 0
						newGrid.rings[i][j].state = 0
					end
				end
				if allNonInfected
					newGrid.rings.pop
					ringCount = ringCount - 1
				end
		end
		newGrid
	end
  
	def findCellNeighbors(ring, ringPos) 
    neighbors = Array.new

		if ring == 0
			neighbors.push Array.[](1, 0)
			neighbors.push Array.[](1, 1)
			neighbors.push Array.[](1, 2)
			neighbors.push Array.[](1, 3)
			neighbors.push Array.[](1, 4)
			neighbors.push Array.[](1, 5)
			return neighbors
		end

		ringSize = ring * 6
    
    # left and right neighbor on same ring
    # adjust for wrapping around by using mod
    neighbors.push Array.[](ring, (ringPos - 1) % ringSize)
    neighbors.push Array.[](ring, (ringPos + 1) % ringSize)

		direction = ringPos / ring
    
 		# check to see if we are on a corner
 		if ringPos % ring == 0 
      
			# we are on a corner, so we have one inner neighbor and three outer neighbors

      # inner ring only has one neighbor
      neighbors.push Array.[](ring - 1, direction * (ring - 1))
      
      # outer ring has three neighbors
      neighbors.push Array.[](ring + 1, (direction * (ring + 1) - 1) % ringSize)
      neighbors.push Array.[](ring + 1, direction * (ring + 1))
      neighbors.push Array.[](ring + 1, (direction * (ring + 1) + 1) % ringSize)

    else

			# we are not on a corner, so we have equal number of inner and outer neighbors

			# distance to the closest corner before this position
			directionPos = ringPos % ring

    	# inner ring has two neighbors
			neighbors.push Array.[](ring - 1, (direction * (ring - 1) + directionPos) % ringSize)
			neighbors.push Array.[](ring - 1, direction * (ring - 1) + directionPos - 1)

			# outer ring also has two neighbors
			neighbors.push Array.[](ring + 1, direction * (ring + 1) + directionPos)
			neighbors.push Array.[](ring + 1, direction * (ring + 1) + directionPos + 1)

    end

		neighbors
  end
end


class Cell < Struct.new(:state) 
end
