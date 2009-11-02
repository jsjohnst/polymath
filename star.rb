require 'rubygems'
require 'RMagick'

canvas = Magick::Image.new(1000, 2000,
    Magick::HatchFill.new('white','lightcyan2'))
@gc = Magick::Draw.new

@gc.stroke('#001aff')
@gc.stroke_width(3)
  @gc.fill('#00ff00')

def draw_hex(x,y, code) 
  color = code == 0 ? "white" : code == 1 ? "green" : "red"
  @gc.fill(color)
  @gc.polygon(x,    y,     
    x+60, y,
    x+90, y+52,
    x+60, y+104,
    x, y+104,
    x-30, y+52)
end
 
 ## 0,0 as center c_x, c_y
 ## x shift delta_x (90)
 ## y shift is delta_y (52)
 
rings = [[0],[1,2,0,2,1,2], [0,0,1,1,2,2,1,1,2,2,0,0], [0,1,2, 0,1,2, 0,1,2,
0,1,2, 0,1,2, 0,1,2]   ]

delta_x = 90
delta_y = 52
center_x = 500 - delta_x / 3
center_y = 1000 - delta_y

rings.each_with_index do |color_array, i|
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
  canvas.write('polygon.gif')

