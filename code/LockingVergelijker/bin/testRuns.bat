@echo off
set /P optimistic=Optimistic? (true or false): %=%
set /P banks=Number of banks (int): %=%
set /P trans=Number of transaction per bank (int): %=%
set /P startNumber=Run number to start with (int): %=%
java banks.ThreadScheduler %banks% %trans% false false %optimistic% > testRuns\%optimistic%\%startNumber%.txt
set /A startNumber = startNumber+1
java banks.ThreadScheduler %banks% %trans% false false %optimistic% > testRuns\%optimistic%\%startNumber%.txt
set /A startNumber = startNumber+1
java banks.ThreadScheduler %banks% %trans% false false %optimistic% > testRuns\%optimistic%\%startNumber%.txt
set /A startNumber = startNumber+1
java banks.ThreadScheduler %banks% %trans% false false %optimistic% > testRuns\%optimistic%\%startNumber%.txt
set /A startNumber = startNumber+1
java banks.ThreadScheduler %banks% %trans% false false %optimistic% > testRuns\%optimistic%\%startNumber%.txt
set /A startNumber = startNumber+1
java banks.ThreadScheduler %banks% %trans% false false %optimistic% > testRuns\%optimistic%\%startNumber%.txt
set /A startNumber = startNumber+1
java banks.ThreadScheduler %banks% %trans% false false %optimistic% > testRuns\%optimistic%\%startNumber%.txt
set /A startNumber = startNumber+1
java banks.ThreadScheduler %banks% %trans% false false %optimistic% > testRuns\%optimistic%\%startNumber%.txt
set /A startNumber = startNumber+1
java banks.ThreadScheduler %banks% %trans% false false %optimistic% > testRuns\%optimistic%\%startNumber%.txt
set /A startNumber = startNumber+1
java banks.ThreadScheduler %banks% %trans% false false %optimistic% > testRuns\%optimistic%\%startNumber%.txt
set /A startNumber = startNumber+1