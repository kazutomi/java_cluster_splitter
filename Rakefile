SetEnv = 'JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8'

task :default => :compile

task :compile do
  sh "env #{SetEnv} javac src/*.java"
end

task :clean do
  sh "rm -f src/*.class"
end
