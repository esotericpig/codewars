#!/usr/bin/env ruby
# encoding: UTF-8
# frozen_string_literal: true

require 'optparse'

LS_RANK_RUBY_VERSION = '2.4'

if defined?(RUBY_VERSION) && RUBY_VERSION < LS_RANK_RUBY_VERSION
  puts "WARN: Ruby v#{RUBY_VERSION} is less than the recommended version of #{LS_RANK_RUBY_VERSION}."
  puts
end

###
# Do for usage: ruby ls_rank.rb --help
#
# Ruby v2.4+ required.
#
# @author Jonathan Bradley Whited
###
class LsRank
  VERSION = '1.2.2'

  BEGIN_TAGS = ['###','/**','"""']
  END_TAGS = ['###',' */','"""']
  PICS_DIR = 'pics'
  RANK_TAGS = ['@rank','rank:','RANK:']
  SEE_TAGS = ['@see','see:','SEE:']

  attr_reader :args
  attr_reader :opts
  attr_reader :parser
  attr_reader :pics
  attr_reader :ranks

  def initialize(args)
    @args = args
    @opts = {}
    @pics = {}
    @ranks = {}

    @parser = OptionParser.new() do |op|
      op.version = VERSION
      op.banner = "Usage: #{op.program_name} [options]"

      op.separator ''
      op.separator 'Options:'
      op.on('-c','--comment','Show header comment block')
      op.on('-m','--markdown','Show markdown for README.md')
      op.on('-r','--rank <rank>','Show code with <rank> kyu only (number; 0 for all)') do |rank|
        rank = rank.to_i()
        rank = nil if rank < 1
        rank
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
      op.separator op.summary_indent + "#{op.program_name} -r 0"
      op.separator op.summary_indent + "#{op.program_name} -c"
      op.separator op.summary_indent + "#{op.program_name} -c -r 7 # Ronaldo?"
      op.separator op.summary_indent + "#{op.program_name} -m"
      op.separator op.summary_indent + "#{op.program_name} -m -r 5"
    end
  end

  def ls()
    ls_pics()

    @ranks = Hash.new{|h,k| h[k] = []}

    Dir.glob(File.join('*','*.*')) do |filename|
      next if filename =~ /#{PICS_DIR}[\/\\]/i

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

      lang = File.dirname(filename)
      name = File.basename(filename,'.*')

      if !(pics_md = @pics[lang][name]).nil?()
        file.pics = "[ #{pics_md.join(' | ')} ]"
      end

      if @opts[:rank].nil?() && !@opts[:markdown]
        # For all ranks (non-markdown), show the files as if flattened
        @ranks[0].push(file)
      else
        @ranks[file.rank].push(file)
      end
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
          print "    - [#{file.filename}](#{file.filename})"
          print " #{file.pics}" unless file.pics.nil?()
          puts
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

  def ls_pics()
    @pics = Hash.new{|h,k| h[k] = {}}

    Dir.glob(File.join(PICS_DIR,'*.*')) do |filename|
      # python_hard_sudoku_solver => python, hard_sudoku_solver
      basename = File.basename(filename)
      basename = basename.split('_',2)

      next if basename.length != 2

      lang = basename[0]
      name = basename[1]
      ext = File.extname(name).strip
      ext = ext[1..-1] if ext.length > 0 # Remove dot
      name = File.basename(name,'.*')

      # For future extensions, do: (gif|png|...)
      next unless ext =~ /(gif)/i

      # Array of markdown
      pics_md = @pics[lang][name]
      @pics[lang][name] = (pics_md = []) if pics_md.nil?()
      pics_md.push("[#{ext}](#{filename})")
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
  attr_accessor :pics
  attr_accessor :rank

  def initialize(filename)
    @comment = ''.dup()
    @filename = filename
    @pics = nil
    @rank = nil
  end

  def <=>(other)
    return @filename.downcase() <=> other.filename.downcase()
  end
end

ls_rank = LsRank.new(ARGV)
ls_rank.parse_opts()
ls_rank.ls()
