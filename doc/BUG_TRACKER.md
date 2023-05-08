# 🐞 BUG TRACKER

## ☑️ 1 - Too many colors in color wheel

> ### Bug Report
> > <b>Name:</b> ~ <br>
> <b>Date:</b> 01.05.2023 <br>
> <b>Contact:</b> ~
> #### Describe the bug
> The color wheel in the output file shows a lot more colors than were supposed to be calculated 
> ##### Steps to reproduce
> Go through the standard steps to generate a color scheme 
> Choose amount of colors > Upload image > download color scheme file 
> ##### Expected behavior
> The color wheel is supposed to show the main colors as shown in the table above 
> ##### Environment
> * OS: Windows 11 
> * Java Version: 20.0.1 
> * Other details that might affect the problem: / 
> ##### Additional context
> Main colors that are shown in the table appear in the color wheel too and thus seem to be calculated correctly
## Solution
### Affected
`ColorWheel.java > createColorWheel(List<BaseColor>) > addCircle(List<BaseColor>)`<br>
`... > OutputColors.java > addContent(ColorData, Document, String) > getColors(ColorData)`<br>
`...... > ColorData.java > getCentroids()`

### Cause
Method `recomputeCentroids(int)` added recalculated centroids back to list without removing outdated centroid.
The new recalculated centroid was supposed to replace the old and outdated one.
This caused the list to grow far beyond its supposed size.

### Fix
### Previous
`protected void recomputeCentroids(int totalCentroids) {`<br>
    `&nbsp;&nbsp;&nbsp;&nbsp;for (int i = 0; i < totalCentroids; i++) {`<br>
        `&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;getCentroids().add(i, calculateCentroid(i));`<br>
    `&nbsp;&nbsp;&nbsp;&nbsp;}`<br>
`}`<br>
### Fixed
`protected void recomputeCentroids(int totalCentroids) {`<br>
    `&nbsp;&nbsp;&nbsp;&nbsp;for (int i = 0; i < totalCentroids; i++) {`<br>
        `&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;getCentroids().remove(i);`💡<br>
        `&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;getCentroids().add(i, calculateCentroid(i));`<br>
    `&nbsp;&nbsp;&nbsp;&nbsp;}`<br>
`}`<br>
