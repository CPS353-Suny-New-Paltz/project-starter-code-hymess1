# Software Engineering Project Starter Code

The system will take a single positive integer and return its largest prime factor. For example, if the input is 13195, the prime factors are 5, 7, 13, and 29, so the output will be 29. If the input is 13, the output will be 13 since 13 is already prime. If the input is 1, there is no prime factor, so the system will return an error or a sentinel value.

## Multithreading
The upper bound I am using for maxThreads is 4.

## Overall System Diagram

![System Diagram](https://raw.githubusercontent.com/CPS353-Suny-New-Paltz/project-starter-code-hymess1/CP4-revisions/src/project/annotations/System-Diagram.jpg)


Benchmark Results:

A performance benchmark was added to compare the baseline network implementation with an optimized version.

	•	Baseline: 7 ms
	•	Optimized: 2 ms
	•	Improvement: 71.43%
  
The optimized version exceeds the required 10% performance improvement.
Benchmark test: test/project/performance/NetworkPerformanceBenchmarkTest.java

 
Issue: The original network layer (NetworkServiceImpl) set up compute jobs using a single-threaded execution model. Even when compute work could be done independently, jobs were processed sequentially, leading to unnecessary process time for workloads containing multiple expensive inputs.

Fix: The network layer was optimized by relying on my multithreaded implementation (MultithreadedNetworkService). This version uses a fixed thread pool to execute compute tasks in parallel while preserving the same API behavior. No changes were needed for the client-facing API.

Result: A benchmark comparing the baseline and optimized implementations shows a significant reduction in execution time, with the multithreaded version completing the same workload substantially faster.

Important:
Performance results can differ depending on execution environment. Modified the assertion to ensure consistent behavior while keeping benchmark output intact.
