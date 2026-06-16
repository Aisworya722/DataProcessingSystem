package main

import (
	"fmt"
	"os"
	"sync"
	"time"
)

type Task struct {
	id int
}

var results []string
var mutex sync.Mutex

func worker(id int, tasks <-chan Task, wg *sync.WaitGroup) {
	defer wg.Done()

	fmt.Printf("Worker %d started\n", id)

	for task := range tasks {
		fmt.Printf("Worker %d processing Task %d\n", id, task.id)

		time.Sleep(500 * time.Millisecond)

		mutex.Lock()
		results = append(results,
			fmt.Sprintf("Worker %d processed Task %d", id, task.id))
		mutex.Unlock()
	}

	fmt.Printf("Worker %d completed\n", id)
}

func main() {
	tasks := make(chan Task)
	var wg sync.WaitGroup

	for i := 1; i <= 3; i++ {
		wg.Add(1)
		go worker(i, tasks, &wg)
	}

	for i := 1; i <= 10; i++ {
		tasks <- Task{id: i}
	}

	close(tasks)

	wg.Wait()

	file, err := os.Create("go_results.txt")
	if err != nil {
		fmt.Println("File creation error:", err)
		return
	}
	defer file.Close()

	for _, result := range results {
		_, err := file.WriteString(result + "\n")
		if err != nil {
			fmt.Println("File write error:", err)
		}
	}

	fmt.Println("Results saved to go_results.txt")
	fmt.Println("All workers finished.")
}