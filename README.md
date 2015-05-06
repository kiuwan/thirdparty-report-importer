# thirdparty-report-importer
A utility for convert a report of third party analyzers to Kiuwan format. Useful to import violations from Fortify to Kiuwan.

# How to compile
Using maven you should write:
<pre>
mvn clean install
</pre>

# How to use
To convert a Fortify report (fortify.xml) to Kiuwan report (kiuwan_fortify.xml) run:
<pre>
java -cp thirdparty-report-importer-0.0.1-SNAPSHOT.jar com.kiuwan.importer.Main Fortify fortify.xml kiuwan_fortify.xml
</pre>
To convert a FxCop report (fxcop.xml) to Kiuwan report (kiuwan_fxcop.xml) run:
<pre>
java -cp thirdparty-report-importer-0.0.1-SNAPSHOT.jar com.kiuwan.importer.Main FxCop fxcop.xml kiuwan_fxcop.xml -base-folder:c:\dotnet_sources
</pre>
