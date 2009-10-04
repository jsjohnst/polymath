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

	def getIteratedGrid
		newGrid = self.clone
		for i in 0..newGrid.rings.length
			for j in 0..newGrid.rings[i].length
				neighbors = findCellNeighbors(i,j)
				randomCell = rand(7)
				# if randomCell == 6, then we selected ourselves, 
				# so no need to change state in new grid
				if(randomCell != 6)
					coords = neighbors[randomCell]
					ring = coords[0]
					ringPos = coords[1]
					if newGrid.rings.length <= ring
						newGrid.rings[ring] = Array.new
						for k in 0..ring*6-1
							newGrid.rings[ring][k] = Cell.new(-1)
						end
					end
					if @rings.length <= ring || @rings[ring].length <= ringPos
						state = 0
					else
						state = @rings[ring][ringPos].state
					end
					if(state > 0)
						newGrid.rings[i][j].state = state
					end
				end
			end
		end
		allNonBorn = true
		for i in 0..newGrid.rings[newGrid.rings.length-1].length-1
			if newGrid.rings[newGrid.rings.length-1][i].state != -1
				allNonBorn = false
				break
			end
		end
		if allNonBorn
			newGrid.rings.pop
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
