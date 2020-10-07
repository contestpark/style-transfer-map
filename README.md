# Style Transfer Application

## Introduction

<p align="center"><img src="https://github.com/contestpark/style-transfer-map/blob/master/pics/main.png" width="40%"></p>

This is a style transfer application using [Fritz AI](https://www.fritz.ai/style-transfer/).
There are 11 filters(Starry Night, The Scream, The Poppy Field, Bicentennial Print from America: The Third Century,
Roy, Les Femmes d’Alger, Head of a Clown, Horses on the Seashore, The Trial, Ritmo Plastico, A view through a kaleidoscope,
Pink and blue rhombuses style) and you can make your photo with style applied by just clicking the filter!

This app also using firebase for login and storing data, but both apis might be expired, so please just infer the code without executing.
Also, if you want to use style transfer api, please refer [this page](https://www.fritz.ai/features/style-transfer.html).


## Activities

```
SplashActivity ─> LoginActivity ─> MainActivity ┬ FragmentAnalysis
                                                └ FragmentCommunity ─> PostDetailActivity
                                                ─> select picture ─> ChangeStyleActivity
```


## Examples

<p align="center"><img src="https://github.com/contestpark/style-transfer-map/blob/master/pics/vertical.png" width="40%"></p>

<p align="center"><img src="https://github.com/contestpark/style-transfer-map/blob/master/pics/horizontal.png" width="40%"></p>
