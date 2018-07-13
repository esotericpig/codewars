#!/usr/bin/env ruby

require 'optparse'

###
# Do one of the following for usage:
# - $ ruby ls_rank.rb --help
# - $ chmod +x ls_rank.rb && ./ls_rank.rb --help
# 
# @author Jonathan Bradley Whited (@esotericpig)
###
class LsRank
  VERSION = '1.1.1'
  
  BEGIN_TAGS = ['###','/**','"""']
  END_TAGS = ['###',' */','"""']
  RANK_TAGS = ['@rank','rank:']
  
  attr_reader :args
  attr_reader :opts
  attr_reader :parser
  attr_reader :ranks
  
  def initialize(args)
    @args = args
    @opts = {}
    @ranks = {}
    
    @parser = OptionParser.new() do |op|
      op.version = VERSION
      op.banner = "Usage: #{op.program_name} [options]"
      
      op.separator ''
      op.separator 'Options:'
      op.on('-c','--comment','Show header comment block')
      op.on('-m','--markdown','Show markdown for README.md')
      op.on('-r','--rank <rank>','Show code with <rank> kyu only (number)') do |rank|
        @opts[:rank] = rank.to_i()
      end
      
      op.separator op.summary_indent + '---'
      op.on('-h','--help','Show help (this)') do
        puts op
        exit
      end
      op.on('-v','--version','Show version') do
        puts "#{op.program_name} v#{op.version}"
        exit
      end
      
      op.separator ''
      op.separator 'Examples:'
      op.separator op.summary_indent + "#{op.program_name} -r 4"
      op.separator op.summary_indent + "#{op.program_name} -r 4 -c"
      op.separator op.summary_indent + "#{op.program_name} -m"
      op.separator op.summary_indent + "#{op.program_name} -m -r 5"
    end
  end
  
  def ls()
    @ranks = Hash.new{|h,k| h[k] = []}
    
    Dir.glob(File.join('*','*.*')) do |filename|
      file = LsRankFile.new(filename)
      parse_comment = false
      
      File.foreach(filename) do |line|
        tag = line.rstrip()
        
        if parse_comment
          file.comment << line
          
          if file.rank.nil?() && RANK_TAGS.any?{|rank_tag| line.include?(rank_tag)}
            file.rank = line.gsub(/\D+/,'').to_i()
            file.rank = nil if file.rank <= 0
          end
          
          break if END_TAGS.include?(tag)
        elsif BEGIN_TAGS.include?(tag)
          file.comment << line
          parse_comment = true
        end
      end
      
      next if file.rank.nil?()
      @ranks[file.rank].push(file)
    end
    
    @ranks.select!{|rank,files| rank == @opts[:rank]} unless @opts[:rank].nil?()
    @ranks = @ranks.sort().to_h()
    
    if @opts[:markdown]
      print '[ '
      @ranks.each_key().with_index() do |rank,i|
        print "[#{rank} kyu](\##{rank}-kyu)"
        print ' | ' if i < (@ranks.length - 1)
      end
      puts ' ]'
      puts
    end
    
    @ranks.each_with_index() do |(rank,files),i|
      if @opts[:markdown]
        puts "- ### [#{rank} kyu](\#by-rank)"
      end
      
      files.sort!()
      files.each_with_index() do |file,j|
        if @opts[:markdown]
          puts "    - [#{file.filename}](#{file.filename})"
        else
          puts file.filename
          
          if @opts[:comment]
            file.comment.split("\n").each() do |line|
              puts @parser.summary_indent + line
            end
            
            puts if (i + j) < (@ranks.length + files.length - 2)
          end
        end
      end
      
      puts if @opts[:markdown]
    end
  end
  
  def parse_opts()
    begin
      @parser.parse(@args,into: @opts)
    rescue OptionParser::InvalidOption=>e
      puts e.message
      puts
      @opts.clear()
    end
    
    if @opts.empty?()
      puts @parser
      exit
    end
  end
end

class LsRankFile
  attr_accessor :comment
  attr_accessor :filename
  attr_accessor :rank
  
  def initialize(filename)
    @comment = ''
    @filename = filename
    @rank = nil
  end
  
  def <=>(other)
    return @filename.downcase() <=> other.filename.downcase()
  end
end

ls_rank = LsRank.new(ARGV)
ls_rank.parse_opts()
ls_rank.ls()
