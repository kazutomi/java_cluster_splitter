ENV.delete('JAVA_TOOL_OPTIONS')
encoding = 'UTF-8'

task :default => :compile

task :compile do
  sh "javac -encoding #{encoding} src/*.java"
end

task :clean do
  sh "rm -f src/*.class"
end
