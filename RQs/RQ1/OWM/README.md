# OWM
This directory contains the experimental results obtained on CANs using OWM to assign strengths for different relationship types.

## compare
This directory contains the *recall* results obtained on each subject system.

## puttogether
This directory contains the *recall* results obtained at each *k* value across all subject systems.

## averageRanking.opj
This file contains the average ranking results of BiMo and baselines at each *k* value. You can use software origin to open this file.

## friedmanResult.csv
This file contains the average ranking results of BiMo and baselines obtained on each software. When calculating the results, we should aggregate the results over all *k* values and then apply the *average ranking of friedman test*.

## friedmanResult.csv_pValue.csv
This file contains the *p*-values returned by the *average ranking of friedman test*. If the *p*-value >=0.05, then the *p*-value will be stored in this file; otherwise, the *p*-values will not be stored.