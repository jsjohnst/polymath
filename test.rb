require "hexagon.rb"
require "rubygems"
require "rmagick"

@gc = Magick::Draw.new

# Have red and green be twice as likely as healthy
def weighted_rand
  r = rand(5)
  r < 1 ? 0 : r < 3 ? 1 : 2
end

def draw_hex(x,y, code) 
  color = code == 0 ? "white" : code == 1 ? "green" : "red"
  @gc.fill(color)
  @gc.polygon(x,    y,     
    x+6, y,
    x+9, y+5.2,
    x+6, y+10.4,
    x, y+10.4,
    x-3, y+5.2)
end

def print_hex_gif(rings, gif_file)
  number_of_cells = rings.flatten.size
  puts number_of_cells
  canvas_size = Math.sqrt(number_of_cells) * 20 
  canvas = Magick::Image.new(canvas_size, canvas_size,
  Magick::HatchFill.new('white', 'white'))

  delta_x = 9
  delta_y = 5.2
  center_x = canvas_size/2 - delta_x / 3
  center_y = canvas_size/2 - delta_y

  rings.each_with_index do |color_array, i|
    print color_array
    if i == 0 
      draw_hex(center_x, center_y, color_array.shift)
      next
    end
      for j in 0..i-1 do 
        draw_hex(center_x+ j*delta_x, center_y - i* 2 * delta_y + j*delta_y, color_array.shift)
      end
      for j in 0..i-1 do 
        draw_hex(center_x + i* delta_x, center_y - i* delta_y + j*2*delta_y, color_array.shift)
      end
      for j in 0..i-1 do 
        draw_hex(center_x + i* delta_x - j*delta_x, center_y + i* delta_y + j*delta_y, color_array.shift)
      end
      for j in 0..i-1 do 
        draw_hex(center_x - j*delta_x, center_y + i*2*delta_y - j*delta_y, color_array.shift)
      end
      for j in 0..i-1 do 
      draw_hex(center_x - i* delta_x, center_y + i*delta_y - j*2*delta_y, color_array.shift)
      end
      for j in 0..i-1 do 
        draw_hex(center_x - i* delta_x + j*delta_x, center_y - i*delta_y - j*delta_y, color_array.shift)
      end
  end
  @gc.draw(canvas)
  canvas.write(gif_file)
end

def simulate_infection(initialization)
  bar = Grid.new(initialization)
    
  5.times do 
    bar = bar.getIteratedGrid
  end

  puts bar.getCurrentState.flatten.size
  print_hex_gif(bar.getCurrentState, "timecourse.gif")
end

## Initialize cells
initializations = Array.new


random_vector_size = 1
for i in 1..2 do 
  random_vector_size += 6*i
end

random_initialization= Array.new
random_vector_size.times do 
  #random_initialization.push(rand(3))
  random_initialization.push(weighted_rand)
end
initializations.push random_initialization



1.times do 
  # copy elements of foo for each run
  initializations.each { |initialization|  simulate_infection(Array.new(initialization) ) }
end
