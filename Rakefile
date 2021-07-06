# encoding: UTF-8
# frozen_string_literal: true


###
# Usage:
#   rake -T
#   rake build -- cpp/build_tower.cc
#   rake run -- cpp/build_tower.cc 33
#   rake build -- java/SimplePigLatin.java
#   rake run -- java/SimplePigLatin.java 'Monkey Burger'
#   rake clean[t] # Dry run.
#   rake clean
#
# @author Jonathan Bradley Whited
###


BUILD_DIR = 'build'

CLEAN_FILES = Rake::FileList[
  "#{BUILD_DIR}/",
  '**/*~','**/*.{class,o,out}',
].exclude(
  '{.git,stock}/**',
)


module Codewars
  @args = nil

  def self.slice_argv!
    return @args unless @args.nil?

    i = ARGV.index('--')

    if i.nil?
      @args = []
    else
      @args = ARGV.slice!(i..-1)
      @args = @args[1..-1] # Remove '--'.
    end

    return @args
  end

  def self.process_file(task_name)
    args = slice_argv!

    filename = args[0].to_s.strip
    basename = File.basename(filename,'.*').strip
    extname = File.extname(filename).strip.downcase

    # Chop off the file.
    args = args[1..-1] unless args.empty?

    if !File.file?(filename)
      abort "File not found: #{filename.inspect}"
    end

    cmd = yield(args,filename,basename,extname)

    if !cmd
      abort "Unimplemented file ext: #{filename.inspect}"
    end

    Rake.sh(*cmd) unless cmd.empty?

    # Was this the last task?
    if ARGV[-1].to_s.strip == task_name
      # Keep Rake quiet.
      exit
    end
  end
end

# Could do 'git clean -nX' instead.
#   However, do NOT use '-d' which would delete 'stock/'.
desc 'Clean artifacts'
task :clean,[:dryrun?] do |t,args|
  dryrun = !args.dryrun?.to_s.strip.empty?

  CLEAN_FILES.each do |filename|
    filename = filename.strip

    if filename.empty? || File.symlink?(filename.sub(%r{[/\\]+\z},''))
      puts "[SKIP] #{filename.inspect}"
      next
    end

    if dryrun
      puts "[DRY ] #{filename}"
    else
      if File.file?(filename)
        puts "[RM  ] #{filename}"
        rm filename,verbose: false
      elsif File.directory?(filename)
        puts "[RMr ] #{filename}"
        rm_r filename,verbose: false,secure: true
      end
    end
  end

  puts "\n=> DRY RUN" if dryrun
end

desc 'Build file: rake build -- <file> <...args>'
task :build do |t|
  Codewars.process_file(t.name) do |args,filename,basename,extname|
    mkdir(BUILD_DIR,verbose: true) unless File.directory?(BUILD_DIR)

    case extname
    when '.cc'
      ['g++','-o',File.join(BUILD_DIR,"#{basename}.o"),filename]
    when '.java'
      ['javac','-d',BUILD_DIR,filename]
    else
      false
    end
  end
end

desc 'Run file: rake run -- <file> <...args>'
task :run do |t|
  Codewars.process_file(t.name) do |args,filename,basename,extname|
    case extname
    when '.coffee'
      ['coffee',filename,*args]
    when '.cc'
      [File.join(BUILD_DIR,"#{basename}.o"),*args]
    when '.cr'
      ['crystal',filename,*args]
    when '.java'
      ['java','-cp',File.join(BUILD_DIR,''),basename,*args]
    when '.js'
      ['node',filename,*args]
    when '.kts'
      ['kotlinc','-script',filename,*args]
    when '.php'
      ['php',filename,*args]
    when '.py'
      ['python',filename,*args]
    when '.rb'
      ['ruby',filename,*args]
    else
      false
    end
  end
end
