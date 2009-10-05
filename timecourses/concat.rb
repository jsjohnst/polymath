

output = File.new("output.txt", "w")
for i in 1..2 
  File.open("output#{i}.txt", "r") do |infile|
    while (line = infile.gets) 
      line.each_char { |c| output.print "#{c} "}
    end
  end
  output.puts "#"
end
output.close
