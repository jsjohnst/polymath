require 'rubygems'
require 'fileutils'

prefix = "trad"
for i in 1..10
  `ruby test.rb`
  FileUtils.mv("output.txt", "timecourses/#{prefix}_output#{i}.txt")
end

output = File.new("timecourses/#{prefix}_output.txt", "w")
for i in 1..10
  File.open("timecourses/#{prefix}_output#{i}.txt", "r") do |infile|
    while (line = infile.gets)
      line.each_byte { |c| output.print "#{c.chr} "}
    end
  end
  output.puts "#"
end
output.close



