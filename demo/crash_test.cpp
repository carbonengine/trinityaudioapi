// Copyright © 2026 CCP ehf.
// Intentionally crashing test used to demo crash dump collection. Do not merge.
int main()
{
    volatile int* pointer = nullptr;
    *pointer = 1;
    return 0;
}
