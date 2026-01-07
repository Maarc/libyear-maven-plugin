def buildLog = new File(basedir, "build.log")
assert !buildLog.text.contains("have newer versions")

def reportFile = new File(basedir, "protobuf-report.csv")
assert reportFile.exists()

def reportContent = reportFile.text
// The report should show 0 libyears for up-to-date dependencies
assert reportContent.contains("com.google.protobuf.nano:protobuf-javanano,3.2.0rc2,jar,Dependency,0.00")