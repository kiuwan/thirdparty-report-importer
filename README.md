# thirdparty-report-importer
A utility for convert a report of third party analyzers to Kiuwan format. Useful to import violations from Fortify, FxCop, RuboCop, BrakeMan or Facebook Infer to Kiuwan.

# Compiled distribution
For your convenience we added to this repository compiled jar archives that you can use directly.
Use thirdparty-report-importer-0.2.3.jar, this one has support for all the above mentioned thiird party analyzers.

# How to compile
Using maven you should write:
<pre>
mvn clean install
</pre>

# How to use
To convert a Fortify report (fortify.xml) to Kiuwan report (kiuwan_fortify.xml) run:
<pre>
java -cp kiuwan-thirdparty-report-importer-0.2.3.jar com.kiuwan.importer.Main Fortify fortify.xml kiuwan_fortify.xml -language=java|cobol
</pre>
To convert a FxCop report (fxcop.xml) to Kiuwan report (kiuwan_fxcop.xml) run:
<pre>
java -cp kiuwan-thirdparty-report-importer-0.2.3.jar com.kiuwan.importer.Main FxCop fxcop.xml kiuwan_fxcop.xml -base-folder=c:\dotnet_sources
</pre>
To convert a RuboCop report (rubocop.json) to Kiuwan report (kiuwan_rubocop.xml) run:
<pre>
java -cp kiuwan-thirdparty-report-importer-0.2.3.jar com.kiuwan.importer.Main RuboCop rubocop.json kiuwan_rubocop.xml
</pre>
To convert a BrakeMan report (brakeman.json) to Kiuwan report (kiuwan_brakeman.xml) run:
<pre>
java -cp kiuwan-thirdparty-report-importer-0.2.3.jar com.kiuwan.importer.Main BrakeMan brakeman.json  kiuwan_brakeman.xml -base-folder=c:\ruby_sources
</pre>
To convert an Infer report (Infer.json) to Kiuwan report (kiuwan_infer.xml) run:
<pre>
java -cp kiuwan-thirdparty-report-importer-0.2.3.jar com.kiuwan.importer.Main Infer infer.json  kiuwan_infer.xml -language=java|c|objectivec
</pre>
