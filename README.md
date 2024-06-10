<a name="readme-top"></a>

<!-- PROJECT LOGO -->
<br />
<div id="center">
  <a href="https://github.com/Fenris22127/ColorSchemeGenerator">
    <img src="src/res/images/CSGLogo.svg" alt="Logo" width="100">
  </a>
  <a id="readme-top"></a>

<h1 id="center">Color Scheme Generator</h1>

  <p id="center">
    This program generates a colour scheme from an image using K-Means clustering and allows the user to save it as a PDF file.
    <br />
    <!--<a href="https://github.com/github_username/repo_name"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/github_username/repo_name">View Demo</a>
    ·
    <a href="https://github.com/github_username/repo_name/issues">Report Bug</a>
    ·
    <a href="https://github.com/github_username/repo_name/issues">Request Feature</a>-->
  </p>
</div>

<!-- TABLE OF CONTENTS -->
<details>
  <summary style="font-size: 18px; margin-top: 20px;">Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li>
          <a href="#installation">Installation</a>
          <ol>
            <li><a href="#using-the-color-scheme-generator">Using the Color Scheme Generator</a></li>
          </ol>
        </li>
      </ul>
    </li>
   <li>
      <a href="#usage">Usage</a>
      <ul>
         <li><a href="#general-use">General Use</a></li>
         <li><a href="#output-file-overview">Output File Overview</a></li>
      </ul>
   </li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>

<!-- ABOUT THE PROJECT -->
<h1>About The Project</h1>
<a id="about-the-project"></a>
<div id="center">
    <a id="center" href="https://github.com/Fenris22127/ColorSchemeGenerator">
        <img src="src/res/images/Page1.svg" alt="Example" width="500">
    </a>
</div>
<p>
   On a wonderful day, the 9th of August 2022 to be exact, during a wonderful holiday in Rostock, Germany to clear my mind from that summer's extreme stress and all things uni, I decided that taking a break is for losers, only the strong shall survive and how better to practice my tolerance to constant exposure to stress than to start a coding project during my four days of vacation for the whole year?
   
   So I stood there in the ocean, gently being rocked back and forth by the waves, intently staring at some water insect that had been trying to swim toward me for several minutes at this point, when I decided to look back at the beach and noticed the white houses, green grass, blue ocean and golden sand.
   And I thought "How nice would it be if I could paint this scene with exactly those colours?",
   well knowing that I hadn't touched paint in ages and that landscape painting was absolutely not my thing.
   
   But that did not matter. My mind had already started racing.

   There were a few problems though. The Wi-Fi of the place we were staying had died and so did my high speed, leaving me with no quick way to read up on anything related to creating a colour scheme from an image.
   
   So there I was, armed with only my phone, my tablet I used for drawing, OneNote and my mind going at 100 km/h. I started thinking and writing wild concepts and ideas until some sort of idea had formed. And because planning is for people who don't know where they are going, I started writing code, since I definitely absolutely knew what I was doing and where I was going.
   
   I remembered my professor's words, that we needed to know how to write code on paper as we wouldn't always have a computer and an IDE available, and I cursed my arrogance as I had thought, that if I needed to code, I would use my PC or an online IDE and if I had neither available, I would just not code.
   So I had to fall back on my very limited memory of my first programming classes.
   Luckily for me, due to my bad memory, I write down everything,
   so I had at least some code snippets I could use as a reference for my first lines of code.
   
   I spent my holiday reading up on K-Means Clustering, three-dimensional spaces in Java and different forms of visualizations of colour spaces. And if I am honest, those holidays were amazing. There is a difference between sitting at home and researching and sitting in the sun at the beach in a different city and researching.
   
   When I came home, I transferred my code into my IDE and to my surprise and joy, I had only very few issues to fix until version 1.0 of my Color Scheme Generator was working.

   Since then, I incorporated the iTextPDF library to be able to save the resulting colour scheme, I became friends or at least decided to call a truce with Gradle to better manage my dependencies and recently even cautiously approached maths to add a colour wheel to the file.

   I have also learned a ton about Java,
   gained a lot of experience with managing a project,
   Code Conventions and most importantly, patience and determination and can finally say:
   I am incredibly proud of my project.
   It might not be a revolutionary tool that people are in dire need of, but it is my first large project that I have kept up with and improved as I continued my studies and that is the result of my determination, patience with myself and my love for coding, which I discovered a few months into my first semester at the University of Applied Sciences in Wernigerode, Germany.
</p>
<h2>Built With</h2>
<a id="built-with"></a>

[![Java][Java.de]][Java-url]


<!-- GETTING STARTED -->
<h1>Getting Started</h1>
<a id="getting-started"></a>

To get a local copy up and running, follow these steps.

<h2> Prerequisites </h2>
<a id="prerequisites"></a>

This program requires Java 8+ to run properly. How to install Java will be explained in the following steps. If your device is not running on a 64-bit operating system, you cannot use this program. You will also need administrator privileges to install Java.

<h2> Installation </h2>
<a id="installation"></a>

<h3> Using the Color Scheme Generator </h3>
<a id="using-the-color-scheme-generator"></a>
1. Download the latest release from the [releases page](https://github.com/Fenris22127/ColorSchemeGenerator/releases)
2. Open the file by double-clicking it

See the [documentation](https://github.com/Fenris22127/ColorSchemeGenerator/blob/master/doc/CSG%20-%20Documentation.pdf) for specifics.

> [!WARNING]
> **NEVER** download or open unknown .exe, .jar or other executable files! <br>
> If you open this without knowing what it is, that is on you.


<!-- USAGE EXAMPLES -->
<h2> Usage </h2>
<a id="usage"></a>
This program can be used to generate a colour scheme from an image.
The number of colours in the colour scheme can be adjusted.
In addition, several more details about the image will be shown in the output file.
These details include the average colour, saturation,
and brightness of the resulting colour scheme as well as some metadata of the image.

<img src="src/res/images/UI.png" alt="The UI explained" width="900">

<h3> General use </h3>
<a id="general-use"></a>

#### 1. Click on the Upload panel.
<img src="src/res/images/upload.svg" alt="Choose the amount of colors" width="300">

#### 2. Choose your image and click on “Open”.
<img src="src/res/images/SelectImage.svg" alt="Select file" width="500">

#### 3. Select, whether you want the colour scheme to be downloaded automatically.
(the file will be saved in your Downloads folder with the name
“ColorScheme_[Filename].pdf”)

<img src="src/res/images/AutoDownload.svg" alt="Choose, if you want your file to be downloaded automatically" width="300">

#### 4. Choose the number of colours you want to be extracted.
<img src="src/res/images/ChooseNumber.svg" alt="Choose the number of colours to be extracted" width="300">

#### 5. Choose, whether you want to add harmonic colours for each extracted colour to the file.
<img src="src/res/images/AddColourHarmonics.svg" alt="Choose if you want colour harmonics" width="300"> <br>
If you decided to add harmonic colours,
the dropdown will open where you will be able to select one or more types of colour harmonies.
See here for an explanation of the different types.
<img src="src/res/images/SelectHarmonics.svg" alt="Select desired colour harmonics" width="300">

#### 6. Click on “Start” or click the button with the upload symbol to choose a different image.
<img src="src/res/images/startProcess.svg" alt="Click 'Start' or upload a new image" width="300">

#### 7. When the process has finished, the download button will be enabled, and you’ll be able to download your colour scheme.
<img src="src/res/images/Download.svg" alt="Click the download button to download your colour scheme" width="150">

#### The file will be called "ColorScheme_[Filename].pdf"
<img src="src/res/images/OutputFile.svg" alt="Icon of the output file" width="100">

<h3> Output File Overview </h3>
<a id="output-file-overview"></a>

<img src="src/res/images/Page1.svg" alt="Overview over page 1" width="500">
<br>
Page 1 shows the file name right below the header.
Right next to the selected image (or below, depending on the image size), the chosen number of main colours are listed.
Their colour values are specified in hexadecimal format, as an HSB triplet and as an RGB triplet.
Below the colours are the colour scheme averages,
listing the average colour (more precisely, the average hue), the average saturation and the average brightness.
Note, that this table might be on page 2 if the image is in landscape format. <br><br>

<img src="src/res/images/Page2.svg" alt="Overview over page 2" width="500"> <br>
Page 2 shows the extracted main colours on a colour wheel. Additionally, the selected image's meta-data is listed.
Pages 3 and 4 list the selected colour harmonics for the main colours.<br><br>
<img src="src/res/images/Page3.svg" alt="Overview over page 3" width="350">
<img src="src/res/images/Page4.svg" alt="Overview over page 4" width="350">
<br>

For further information about the metadata provided on page 2, please refer to the [documentation](https://github.com/Fenris22127/ColorSchemeGenerator/blob/master/doc/CSG%20-%20Documentation.pdf).

<!-- ROADMAP -->
<h2> Roadmap </h2>
<a id="roadmap"></a>

- [x] Finish README
  - [x] Add Usage Examples
  - [x] Add License
- [x] Create Bug Template
- [x] Add documentation
- [ ] ~~Create Commit message guidelines~~
- [x] Add history
- [x] Add Metadata to the image
- [x] Better incorporate colour wheel
- [x] Implement multiple languages (maybe)
- [x] Create an interface using JavaFX
- [ ] ~~Implement the possibility of seeing the result in the interface already~~
- [x] Write a file explaining the mathematical structures used (see: Documentation)
- [x] Write a file explaining the mathematical structures used in simple terms (see: Documentation)
- [x] Explain code (maybe, JavaDocs exist) (see: Documentation)
- [ ] Create a wiki

See the [open issues](https://github.com/Fenris22127/ColorSchemeGenerator/issues) for a full list of proposed features (and known issues).<br>
To report an issue, please use the [issue template](https://github.com/Fenris22127/ColorSchemeGenerator/blob/master/doc/ISSUE_TEMPLATE.md).


<!-- LICENSE -->
<h2> License </h2>
<a id="license"></a>
By using the software, the user agrees to the 
[End User License Agreement](https://github.com/Fenris22127/ColorSchemeGenerator/blob/master/doc/LICENSE.md) and accepts 
the risks of use.

<!-- CONTACT -->
<h2> Contact </h2>
<a id="contact"></a>

Elisa Johanna Woelk<br><br>

![Email][email-shield]<br>
<!-- [![Twitter][twitter-shield]][twitter-url]<br> -->
[![Instagram][instagram-shield]][instagram-url]<br>
[![GitHub][github-shield]][github-url]

Project Link: [https://github.com/Fenris22127/ColorSchemeGenerator](https://github.com/Fenris22127/ColorSchemeGenerator)

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/github_username/repo_name.svg?style=for-the-badge
[contributors-url]: https://github.com/github_username/repo_name/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/github_username/repo_name.svg?style=for-the-badge
[forks-url]: https://github.com/github_username/repo_name/network/members
[stars-shield]: https://img.shields.io/github/stars/github_username/repo_name.svg?style=for-the-badge
[stars-url]: https://github.com/Fenris_22127/ColorSchemeGenerator/stargazers
[issues-shield]: https://img.shields.io/github/issues/github_username/repo_name.svg?style=for-the-badge
[issues-url]: https://github.com/github_username/repo_name/issues
[license-shield]: https://img.shields.io/github/license/github_username/repo_name.svg?style=for-the-badge
[license-url]: https://github.com/github_username/repo_name/blob/master/LICENSE.txt
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://linkedin.com/in/linkedin_username
[product-screenshot]: images/screenshot.png
[email-shield]: https://img.shields.io/badge/--black.svg?style=for-the-badge&logo=microsoftoutlook&colorB=555&label=elisa-johanna.woelk@outlook.de
[twitter-shield]: https://img.shields.io/badge/-Twitter-black.svg?style=for-the-badge&logo=Twitter&colorB=555
[twitter-url]: https://twitter.com/@fenris_22127
[github-shield]: https://img.shields.io/badge/-Github-black.svg?style=for-the-badge&logo=github&colorB=555
[github-url]: https://github.com/Fenris22127
[instagram-shield]: https://img.shields.io/badge/-instagram-black.svg?style=for-the-badge&logo=instagram&colorB=555
[instagram-url]: https://www.instagram.com/fenris_22127/
[Java-url]: https://www.java.com/
[Java.de]: https://img.shields.io/badge/java-41492f?style=for-the-badge&logoColor=fafbfc
[Close-x]: https://img.shields.io/badge/%E2%9C%95-red?style=flat&logoColor=green
